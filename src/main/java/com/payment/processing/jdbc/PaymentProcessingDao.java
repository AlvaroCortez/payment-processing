package com.payment.processing.jdbc;

import com.payment.processing.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentProcessingDao {
    void uploadPayment(List<Payment> payments);

    BigDecimal getTotalAmount(String sender);
}
