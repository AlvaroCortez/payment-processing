package com.payment.processing.jdbc;

import com.payment.processing.configuration.DataSourceProperties;
import com.payment.processing.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

public class PaymentProcessingDaoImplTest {
    private final DataSource firstDataSource = mock(DataSource.class);
    private final DataSource secondDataSource = mock(DataSource.class);
    private final DataSource thirdDataSource = mock(DataSource.class);
    private final Statement statement = mock(Statement.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final Connection connection = mock(Connection.class);
    private final Map<Integer, DataSource> dbNumberDataSource = new HashMap<>();
    private final DataSourceProperties dataSourceProperties = mock(DataSourceProperties.class);
    private final PaymentProcessingDaoImpl paymentProcessingDao = new PaymentProcessingDaoImpl(dataSourceProperties);

    @BeforeEach
    public void init() throws SQLException {
        dbNumberDataSource.put(0, firstDataSource);
        dbNumberDataSource.put(1, secondDataSource);
        dbNumberDataSource.put(2, thirdDataSource);
        when(dataSourceProperties.getDbNumberDataSource()).thenReturn(dbNumberDataSource);
        when(dataSourceProperties.getDatabaseCount()).thenReturn(3);
        when(firstDataSource.getConnection()).thenReturn(connection);
        when(secondDataSource.getConnection()).thenReturn(connection);
        when(thirdDataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(paymentProcessingDao.insertPayment)).thenReturn(preparedStatement);
    }

    @Test
    public void uploadPayments() throws SQLException {
        final List<Payment> paymentsToSecondDb = asList(Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build(),
                Payment.builder().sender("test").receiver("test receiver").amount(valueOf(205.34)).build());
        final List<Payment> paymentsToThirdDb = singletonList(Payment.builder().sender("miracle").receiver("test receiver").amount(valueOf(205.34)).build());
        final List<Payment> payments = Stream.concat(paymentsToThirdDb.stream(), paymentsToSecondDb.stream()).collect(Collectors.toList());

        paymentProcessingDao.uploadPayment(payments);

        verify(preparedStatement, times(2)).executeBatch();
        verify(connection, times(2)).commit();
    }

    //todo test get total amount
}
