package com.payment.processing.util;

import com.payment.processing.model.Payment;
import org.junit.jupiter.api.Test;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtilTest {

    @Test
    public void readJson() {
        final Payment payment = Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build();
        final Payment readPayment = TestUtil.readJson("data/testutil/payment1.json", Payment.class);
        assertEquals(payment, readPayment);
    }

    @Test
    public void writeJson() {
        final Payment payment = Payment.builder().sender("test sender").receiver("test receiver").amount(valueOf(205.34)).build();
        TestUtil.writeJson(payment, "data/testutil/writedPayment.json");
        final Payment readPayment = TestUtil.readJson("data/testutil/writedPayment.json", Payment.class);
        assertEquals(payment, readPayment);
    }
}
