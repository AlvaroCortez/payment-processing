package com.payment.processing.jdbc;

import com.payment.processing.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Rollback
public class PaymentProcessingDaoImplTestIT {

    @Autowired
    private PaymentProcessingDao paymentProcessingDao;

    @Test
    public void insertPayments() {
        final List<Payment> payments = singletonList(Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build());

        paymentProcessingDao.uploadPayment(payments);
    }

    @Test
    public void getTotalAmount() {
        final String sender = "test sender";
        final List<Payment> payments = asList(Payment.builder().sender(sender).receiver("test receiver").amount(valueOf(205.34)).build(),
                Payment.builder().sender(sender).receiver("test receiver").amount(valueOf(2050.666)).build(),
                Payment.builder().sender("test").receiver("test receiver").amount(valueOf(205.34)).build());

        paymentProcessingDao.uploadPayment(payments);

        assertEquals(valueOf(2256.006), paymentProcessingDao.getTotalAmount(sender));
    }

    @Test
    public void insertPayments_emptyList() {
        paymentProcessingDao.uploadPayment(emptyList());
    }
}
