package com.payment.processing.controller;

import com.payment.processing.exception.BadArgumentException;
import com.payment.processing.jdbc.PaymentProcessingDao;
import com.payment.processing.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
public class PaymentProcessingController {

    private final PaymentProcessingDao paymentProcessingDao;

    @GetMapping("/totalAmount/{sender}")
    public BigDecimal getTotalAmount(@PathVariable("sender") String sender) {
        return paymentProcessingDao.getTotalAmount(sender);
    }

    @PostMapping
    public void uploadPayments(@RequestBody List<Payment> payments) {
        validatePayments(payments);
        paymentProcessingDao.uploadPayment(payments);
    }

    private void validatePayments(List<Payment> payments) {
        final boolean violatedPayment = payments.stream()
                .anyMatch(payment -> isNull(payment.getSender()) || isNull(payment.getReceiver()) || isNull(payment.getAmount()));
        if (violatedPayment) {
            throw new BadArgumentException("There is should not be payment with empty sender, receiver or amount");
        }
    }
}
