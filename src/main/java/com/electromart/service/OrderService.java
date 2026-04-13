package com.electromart.service;

import com.electromart.dto.OrderDto;
import com.electromart.entity.Order;
import java.util.List;

public interface OrderService {

    Order createOrderFromCurrentCart(Long addressId);

    List<OrderDto> getOrdersForCurrentUser();

    OrderDto getOrderDetails(String orderNumber);

    Order getOrderEntity(String orderNumber);

    void markOrderPaid(String orderNumber, String paymentReference);

    OrderDto updateOrder(String orderNumber, OrderDto updatedDto);

    OrderDto cancelOrder(String orderNumber);
}
