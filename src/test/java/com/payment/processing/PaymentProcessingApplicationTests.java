package com.payment.processing;

import com.payment.processing.controller.PaymentProcessingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentProcessingApplicationTests {

    @Autowired
    private PaymentProcessingController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
