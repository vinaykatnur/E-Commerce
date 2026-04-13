package com.electromart.controller;

import com.electromart.entity.Order;
import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.service.OrderService;
import com.electromart.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping(value = "/orders", produces = MediaType.TEXT_HTML_VALUE)
    public String orders(Model model) {
        log.info("Rendering orders page");
        model.addAttribute("orders", orderService.getOrdersForCurrentUser());
        return "orders";
    }

    @PostMapping("/orders/checkout")
    public String checkout(@RequestParam(required = false) Long addressId, RedirectAttributes redirectAttributes) {
        log.info("Checkout requested with addressId={}", addressId);
        try {
            Order order = orderService.createOrderFromCurrentCart(addressId);
            log.info("Checkout order {} created successfully", order.getOrderNumber());
            return "redirect:/orders/checkout/" + order.getOrderNumber();
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Checkout failed for addressId={}: {}", addressId, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders/checkout/{orderNumber}")
    public String checkoutPage(@PathVariable String orderNumber, Model model, RedirectAttributes redirectAttributes) {
        log.info("Rendering checkout page for order {}", orderNumber);
        try {
            Order order = orderService.getOrderEntity(orderNumber);
            model.addAttribute("order", orderService.getOrderDetails(orderNumber));
            model.addAttribute("payment", paymentService.createPaymentOrder(order));
            log.info("Checkout page ready for order {}", orderNumber);
            return "checkout";
        } catch (AppException | ResourceNotFoundException exception) {
            log.warn("Checkout page failed for order {}: {}", orderNumber, exception.getMessage());
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/orders";
        }
     }
}
