package com.electromart.controller;

import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/verify")
    public String verifyPayment(@RequestParam String appOrderNumber,
                                @RequestParam(required = false) String razorpay_payment_id,
                                @RequestParam(required = false) String razorpay_order_id,
                                @RequestParam(required = false) String razorpay_signature,
                                RedirectAttributes redirectAttributes) {
        log.info("Verify payment requested for order {}", appOrderNumber);
        try {
            paymentService.verifyAndCapturePayment(appOrderNumber, razorpay_payment_id, razorpay_order_id, razorpay_signature);
            redirectAttributes.addFlashAttribute("success", "Payment completed successfully.");
            log.info("Payment verified successfully for order {}", appOrderNumber);
            return "redirect:/orders";
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Payment verification failed for order {}: {}", appOrderNumber, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/orders/checkout/" + appOrderNumber;
        }
    }

    @PostMapping("/payments/demo-confirm/{orderNumber}")
    public String demoConfirm(@PathVariable String orderNumber, RedirectAttributes redirectAttributes) {
        log.info("Demo payment requested for order {}", orderNumber);
        try {
            paymentService.verifyAndCapturePayment(orderNumber, null, null, null);
            redirectAttributes.addFlashAttribute("success", "Demo payment recorded successfully.");
            log.info("Demo payment completed for order {}", orderNumber);
            return "redirect:/orders";
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Demo payment failed for order {}: {}", orderNumber, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/orders/checkout/" + orderNumber;
        }
    }
}
