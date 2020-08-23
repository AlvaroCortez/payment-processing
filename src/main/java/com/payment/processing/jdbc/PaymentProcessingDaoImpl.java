package com.payment.processing.jdbc;

import com.payment.processing.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

@Service
@AllArgsConstructor
public class PaymentProcessingDaoImpl implements PaymentProcessingDao {
    private static final int NUMBER_OF_DATABASES = 3;

    @Autowired
    @Qualifier("firstJdbcTemplate")
    private final JdbcTemplate firstJdbcTemplate;
    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private final JdbcTemplate secondaryJdbcTemplate;
    @Autowired
    @Qualifier("thirdJdbcTemplate")
    private final JdbcTemplate thirdJdbcTemplate;

    final String selectTotalAmount = "SELECT SUM(amount) FROM PAYMENT WHERE sender = ?";
    final String insertPayment = "INSERT INTO PAYMENT (sender, receiver, amount) VALUES (?, ?, ?)";

    @Override
    @Transactional
    public void uploadPayment(List<Payment> payments) {
        final boolean violatedPayment = payments.stream()
                .anyMatch(payment -> isNull(payment.getSender()) || isNull(payment.getReceiver()) || isNull(payment.getAmount()));
        if (violatedPayment) {
            throw new IllegalArgumentException("There is should not be payment with empty sender, receiver or amount");
        }

        final Map<Integer, List<Payment>> dbPayments = payments.stream()
                .collect(Collectors.groupingBy(payment -> getDatabaseNumber(payment.getSender())));
        //todo replace 0 1 2
        final List<Payment> firstDbPayments = dbPayments.getOrDefault(0, emptyList());
        final List<Payment> secondDbPayments = dbPayments.getOrDefault(1, emptyList());
        final List<Payment> thirdDbPayments = dbPayments.getOrDefault(2, emptyList());

        firstJdbcTemplate.batchUpdate(insertPayment, firstDbPayments, firstDbPayments.size(), paymentStatementSetter());
        secondaryJdbcTemplate.batchUpdate(insertPayment, secondDbPayments, secondDbPayments.size(), paymentStatementSetter());
        thirdJdbcTemplate.batchUpdate(insertPayment, thirdDbPayments, thirdDbPayments.size(), paymentStatementSetter());
    }

    @Override
    public BigDecimal getTotalAmount(String sender) {
        final int databaseNumber = getDatabaseNumber(sender);
        switch (databaseNumber) {
            case 0:
                return firstJdbcTemplate.queryForObject(selectTotalAmount, BigDecimal.class, sender);
            case 1:
                return secondaryJdbcTemplate.queryForObject(selectTotalAmount, BigDecimal.class, sender);
            case 2:
                return thirdJdbcTemplate.queryForObject(selectTotalAmount, BigDecimal.class, sender);
            default:
                throw new IllegalArgumentException("Unexpected database number");
        }
    }

    ParameterizedPreparedStatementSetter<Payment> paymentStatementSetter() {
        return (ps, payment) -> {
            ps.setString(1, payment.getSender());
            ps.setString(2, payment.getReceiver());
            ps.setBigDecimal(3, payment.getAmount());
        };
    }

    private int getDatabaseNumber(String sender) {
        return abs(sender.hashCode() % NUMBER_OF_DATABASES);
    }
}
