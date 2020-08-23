package com.payment.processing.controller;

import com.payment.processing.jdbc.PaymentProcessingDao;
import com.payment.processing.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
public class PaymentProcessingController {

    private final PaymentProcessingDao paymentProcessingDao;

    //todo pathVariable or pathParam?
    @GetMapping("/totalAmount/{sender}")
    public BigDecimal getTotalAmount(@PathVariable("sender") String sender) {
        return paymentProcessingDao.getTotalAmount(sender);
    }

    @PostMapping
    public void uploadPayments(@RequestBody List<Payment> payments) {
        paymentProcessingDao.uploadPayment(payments);
    }
}
