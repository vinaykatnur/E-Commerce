package com.electromart.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentOrderDto {
    private boolean enabled;
    private String keyId;
    private String razorpayOrderId;
    private String currency;
    private String orderNumber;
    private BigDecimal totalAmount;
    private long amountInPaise;
}
