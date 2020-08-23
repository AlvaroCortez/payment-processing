package com.payment.processing.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder(toBuilder = true)
public class Payment {
    String sender;
    String receiver;
    BigDecimal amount;
}
