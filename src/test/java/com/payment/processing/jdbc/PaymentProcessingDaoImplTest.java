package com.payment.processing.jdbc;

import com.payment.processing.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentProcessingDaoImplTest {
    private final JdbcTemplate firstJdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcTemplate secondJdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcTemplate thirdJdbcTemplate = mock(JdbcTemplate.class);
    private final PaymentProcessingDaoImpl paymentProcessingDao = new PaymentProcessingDaoImpl(firstJdbcTemplate, secondJdbcTemplate, thirdJdbcTemplate);

    @Test
    public void uploadPayments() {
        final List<Payment> payments = singletonList(Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build());
        final ParameterizedPreparedStatementSetter<Payment> paymentStatementSetter = paymentProcessingDao.paymentStatementSetter();
        final String insertPayment = paymentProcessingDao.insertPayment;

        paymentProcessingDao.uploadPayment(payments);

        verify(firstJdbcTemplate).batchUpdate(insertPayment, emptyList(), 0, paymentStatementSetter);
        verify(secondJdbcTemplate).batchUpdate(insertPayment, payments, payments.size(), paymentStatementSetter);
        verify(thirdJdbcTemplate).batchUpdate(insertPayment, emptyList(), 0, paymentStatementSetter);
    }

    @Test
    public void uploadNullablePayments() {
        final List<Payment> payments = singletonList(Payment.builder().sender("test sender").receiver("test receiver").build());

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> paymentProcessingDao.uploadPayment(payments));

        final String expectedException = "There is should not be payment with empty sender, receiver or amount";

        assertEquals(expectedException, exception.getMessage());
    }

    //todo tgest get total amount
}
