package com.electromart.service.impl;

import com.electromart.config.RazorpayProperties;
import com.electromart.dto.PaymentOrderDto;
import com.electromart.entity.Order;
import com.electromart.exception.AppException;
import com.electromart.service.OrderService;
import com.electromart.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayProperties razorpayProperties;
    private final OrderService orderService;

    @Override
    public PaymentOrderDto createPaymentOrder(Order order) {
        long amountInPaise = order.getTotalAmount().multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        if (!isPaymentEnabled()) {
            return PaymentOrderDto.builder()
                    .enabled(false)
                    .currency(razorpayProperties.currency())
                    .orderNumber(order.getOrderNumber())
                    .totalAmount(order.getTotalAmount())
                    .amountInPaise(amountInPaise)
                    .build();
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayProperties.keyId(), razorpayProperties.keySecret());
            JSONObject request = new JSONObject();
            request.put("amount", amountInPaise);
            request.put("currency", razorpayProperties.currency());
            request.put("receipt", order.getOrderNumber());
            request.put("payment_capture", 1);
            com.razorpay.Order razorpayOrder = client.orders.create(request);

            return PaymentOrderDto.builder()
                    .enabled(true)
                    .keyId(razorpayProperties.keyId())
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .currency(razorpayProperties.currency())
                    .orderNumber(order.getOrderNumber())
                    .totalAmount(order.getTotalAmount())
                    .amountInPaise(amountInPaise)
                    .build();
        } catch (Exception ex) {
            throw new AppException("Unable to initialize Razorpay payment for this order.");
        }
    }

    @Override
    public boolean isPaymentEnabled() {
        return razorpayProperties.enabled() && razorpayProperties.hasCredentials();
    }

    @Override
    public void verifyAndCapturePayment(String appOrderNumber, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        if (!isPaymentEnabled()) {
            orderService.markOrderPaid(appOrderNumber, "DEMO-" + appOrderNumber);
            return;
        }
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);
            if (!Utils.verifyPaymentSignature(options, razorpayProperties.keySecret())) {
                throw new AppException("Razorpay payment signature validation failed.");
            }
            orderService.markOrderPaid(appOrderNumber, razorpayPaymentId);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AppException("Unable to verify Razorpay payment.");
        }
    }
}
