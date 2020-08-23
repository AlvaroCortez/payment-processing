package com.payment.processing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.processing.jdbc.PaymentProcessingDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
