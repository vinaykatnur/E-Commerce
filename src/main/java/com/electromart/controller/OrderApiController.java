package com.electromart.controller;

import com.electromart.dto.OrderDto;
import com.electromart.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDto> getOrders() {
        log.info("Orders API called");
        List<OrderDto> response = orderService.getOrdersForCurrentUser();
        log.info("Orders API completed with {} orders", response.size());
        return response;
    }

    @PutMapping("/order/cancel/{orderNumber}")
    public OrderDto cancelOrder(@PathVariable String orderNumber) {
        log.info("Cancel order API called for {}", orderNumber);
        OrderDto response = orderService.cancelOrder(orderNumber);
        log.info("Cancel order API completed for {} with status {}", orderNumber, response.getStatus());
        return response;
    }

    @PutMapping("/order/modified/{orderNumber}")
    public OrderDto updateOrder(@PathVariable String orderNumber, @RequestBody OrderDto updatedDto) {
        log.info("Update order API called for {} with status={} paymentStatus={}",
                orderNumber, updatedDto.getStatus(), updatedDto.getPaymentStatus());
        OrderDto response = orderService.updateOrder(orderNumber, updatedDto);
        log.info("Update order API completed for {} with status={} paymentStatus={}",
                orderNumber, response.getStatus(), response.getPaymentStatus());
        return response;
    }
}
