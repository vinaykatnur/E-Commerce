package com.electromart.service;

import com.electromart.dto.PaymentOrderDto;
import com.electromart.entity.Order;

public interface PaymentService {

    PaymentOrderDto createPaymentOrder(Order order);

    boolean isPaymentEnabled();

    void verifyAndCapturePayment(String appOrderNumber, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature);
}
