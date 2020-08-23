package com.payment.processing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.processing.exception.BadArgumentException;
import com.payment.processing.jdbc.PaymentProcessingDao;
import com.payment.processing.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PaymentProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentProcessingDao paymentProcessingDao;

    @Test
    public void getTotalAmount() throws Exception {
        final String sender = "sender";
        final BigDecimal totalAmount = BigDecimal.valueOf(1_000_000);

        when(paymentProcessingDao.getTotalAmount(sender)).thenReturn(totalAmount);

        mockMvc.perform(get("/payment/totalAmount/{sender}", sender))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(totalAmount)));
    }

    @Test
    public void uploadPayments() throws Exception {
        final List<Payment> payments = singletonList(Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build());

        mockMvc.perform(post("/payment", payments))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void uploadNullablePayments() throws Exception {
        final List<Payment> payments = singletonList(Payment.builder().sender("test sender").receiver("test receiver").build());

        final String expectedException = "There is should not be payment with empty sender, receiver or amount";

        mockMvc.perform(post("/payment", payments))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentException))
                .andExpect(result -> assertEquals(expectedException, result.getResolvedException().getMessage()));
    }
}
