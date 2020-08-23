package com.payment.processing.jdbc;

import com.payment.processing.configuration.DataSourceProperties;
import com.payment.processing.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PaymentProcessingDaoImplTest {
    private final JdbcTemplate firstJdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcTemplate secondJdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcTemplate thirdJdbcTemplate = mock(JdbcTemplate.class);
    private final Map<Integer, JdbcTemplate> dbNumberJdbcTemplate = new HashMap<>();
    private final DataSourceProperties dataSourceProperties = mock(DataSourceProperties.class);
    private final PaymentProcessingDaoImpl paymentProcessingDao = new PaymentProcessingDaoImpl(dataSourceProperties);
    //    private final PaymentProcessingDaoImpl paymentProcessingDao = new PaymentProcessingDaoImpl(firstJdbcTemplate, secondJdbcTemplate, thirdJdbcTemplate);

    @BeforeEach
    public void init() {
        dbNumberJdbcTemplate.put(0, firstJdbcTemplate);
        dbNumberJdbcTemplate.put(1, secondJdbcTemplate);
        dbNumberJdbcTemplate.put(2, thirdJdbcTemplate);
        when(dataSourceProperties.getDbNumberJdbcTemplate()).thenReturn(dbNumberJdbcTemplate);
        when(dataSourceProperties.getDatabaseCount()).thenReturn(3);
    }

    @Test
    public void uploadPayments() {
        final List<Payment> paymentsToSecondDb = asList(Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build(),
                Payment.builder().sender("test").receiver("test receiver").amount(valueOf(205.34)).build());
        final List<Payment> paymentsToThirdDb = singletonList(Payment.builder().sender("miracle").receiver("test receiver").amount(valueOf(205.34)).build());
        final List<Payment> payments = Stream.concat(paymentsToThirdDb.stream(), paymentsToSecondDb.stream()).collect(Collectors.toList());
        final ParameterizedPreparedStatementSetter<Payment> paymentStatementSetter = paymentProcessingDao.paymentStatementSetter();
        final String insertPayment = paymentProcessingDao.insertPayment;

        paymentProcessingDao.uploadPayment(payments);

        verify(secondJdbcTemplate).batchUpdate(insertPayment, paymentsToSecondDb, paymentsToSecondDb.size(), paymentStatementSetter);
        verify(thirdJdbcTemplate).batchUpdate(insertPayment, paymentsToThirdDb, paymentsToThirdDb.size(), paymentStatementSetter);
//        verify(thirdJdbcTemplate).batchUpdate(insertPayment, emptyList(), 0, paymentStatementSetter);
    }

    //todo tgest get total amount
}
