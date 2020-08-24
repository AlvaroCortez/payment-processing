package com.payment.processing.jdbc;

import com.payment.processing.configuration.DataSourceProperties;
import com.payment.processing.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class PaymentProcessingDaoImpl implements PaymentProcessingDao {
    private final DataSourceProperties dataSourceProperties;

    final String selectTotalAmount = "SELECT SUM(amount) FROM PAYMENT WHERE sender = ?";
    final String insertPayment = "INSERT INTO PAYMENT (sender, receiver, amount) VALUES (?, ?, ?)";
    final String checkConnection = "SELECT 1 FROM PAYMENT";

    @Override
    public void uploadPayment(List<Payment> payments) {
        final List<DataSource> aliveDataSources = getAliveDataSources(dataSourceProperties.getDbNumberDataSource().values());
        if (aliveDataSources.isEmpty()) {
            throw new RuntimeException("There is no alive databases");
        }
        final Map<Integer, List<Payment>> dbPayments = payments.stream()
                .collect(Collectors.groupingBy(payment -> getDatabaseNumber(payment.getSender(), aliveDataSources.size())));

        final List<Connection> connections = new LinkedList<>();

        //todo remove try in try
        dbPayments.keySet().forEach(dbNumber -> {
            try {
                connections.add(batchInsert(aliveDataSources.get(dbNumber), dbPayments.get(dbNumber)));
            } catch (SQLException throwables) {
                try {
                    rollbackTransactions(connections);
                } catch (SQLException e) {
                    throw new RuntimeException("Something went wrong when rollback transactions");
                }
                throw new RuntimeException("Something went wrong when add payments");
            }
        });

        try {
            commitTransactions(connections);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public BigDecimal getTotalAmount(String sender) {
        final List<DataSource> aliveDataSources = getAliveDataSources(dataSourceProperties.getDbNumberDataSource().values());
        if (aliveDataSources.isEmpty()) {
            throw new RuntimeException("There is no alive databases");
        }
        if (aliveDataSources.size() != dataSourceProperties.getDatabaseCount()) {
            throw new RuntimeException("There is a database that not active. Cannot calculate total amount");
        }

        try {
            BigDecimal totalAmount = ZERO;
            for (DataSource dataSource : aliveDataSources) {
                BigDecimal amount = getTotalAmount(dataSource, sender);
                totalAmount = totalAmount.add(amount);
            }
            return totalAmount;
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong when get total amount");
        }
    }

    private BigDecimal getTotalAmount(DataSource dataSource, String sender) throws SQLException {
        Connection connection = dataSource.getConnection();
        final PreparedStatement statement = connection.prepareStatement(selectTotalAmount);
        statement.setString(1, sender);
        final ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            final BigDecimal totalAmount = resultSet.getBigDecimal(1);
            if (nonNull(totalAmount)) {
                return totalAmount;
            }
        }
        return ZERO;
    }

    private Connection batchInsert(DataSource dataSource, List<Payment> payments) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        final PreparedStatement statement = connection.prepareStatement(insertPayment);
        for (Payment payment : payments) {
            addPaymentToStatement(statement, payment);
        }

        statement.executeBatch();

        statement.close();
        return connection;
    }

    private void addPaymentToStatement(PreparedStatement statement, Payment payment) throws SQLException {
        statement.setString(1, payment.getSender());
        statement.setString(2, payment.getReceiver());
        statement.setBigDecimal(3, payment.getAmount());
        statement.addBatch();
    }

    private void commitTransactions(List<Connection> connections) throws SQLException {
        for (Connection connection : connections) {
            connection.commit();
            connection.close();
        }
    }

    private void rollbackTransactions(List<Connection> connections) throws SQLException {
        for (Connection connection : connections) {
            connection.rollback();
            connection.close();
        }
    }

    private int getDatabaseNumber(String sender, int databaseCount) {
        return abs(sender.hashCode() % databaseCount);
    }

    private List<DataSource> getAliveDataSources(Collection<DataSource> dataSourceList) {
        final List<DataSource> dataSources = new LinkedList<>();
        for (DataSource dataSource : dataSourceList) {
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(true);
                final Statement statement = connection.createStatement();
                statement.execute(checkConnection);

                statement.executeBatch();

                statement.close();
                dataSources.add(dataSource);
            } catch (SQLException ignored) {

            }
        }
        return dataSources;
    }
}
