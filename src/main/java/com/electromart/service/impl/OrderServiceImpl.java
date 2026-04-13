package com.electromart.service.impl;

import com.electromart.dto.AddressDto;
import com.electromart.dto.OrderDto;
import com.electromart.dto.OrderItemDto;
import com.electromart.entity.Address;
import com.electromart.entity.Cart;
import com.electromart.entity.CartItem;
import com.electromart.entity.Order;
import com.electromart.entity.OrderItem;
import com.electromart.entity.OrderStatus;
import com.electromart.entity.PaymentStatus;
import com.electromart.entity.Product;
import com.electromart.exception.AppException;
import com.electromart.exception.ResourceNotFoundException;
import com.electromart.repository.OrderRepository;
import com.electromart.repository.ProductRepository;
import com.electromart.service.AddressService;
import com.electromart.service.CartService;
import com.electromart.service.CurrentUserService;
import com.electromart.service.OrderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final CurrentUserService currentUserService;
    private final AddressService addressService;

    @Override
    @Transactional
    public Order createOrderFromCurrentCart(Long addressId) {
        log.info("Creating order from cart for addressId={}", addressId);
        Cart cart = cartService.getCurrentUserCartEntity();
        if (cart.getItems().isEmpty()) {
            throw new AppException("Your cart is empty.");
        }
        if (addressId == null) {
            throw new AppException("Please select a delivery address before checkout.");
        }

        Address address = addressService.getAddressEntityForCurrentUser(addressId);

        Order order = new Order();
        order.setUser(currentUserService.getCurrentUser());
        order.setAddress(address);
        order.setOrderNumber("EMI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setPaymentProvider("RAZORPAY");

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findByIdAndActiveTrue(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not available anymore."));
            if (cartItem.getQuantity() > product.getStock()) {
                throw new AppException(product.getName() + " is no longer available in the requested quantity.");
            }
            product.setStock(product.getStock() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setTotalPrice(lineTotal);
            subtotal = subtotal.add(lineTotal);
            items.add(orderItem);
        }

        order.setSubtotal(subtotal);
        order.setTotalAmount(subtotal);
        order.setItems(items);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart);
        log.info("Order {} created with {} items and total {}", savedOrder.getOrderNumber(), items.size(), savedOrder.getTotalAmount());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersForCurrentUser() {
        Long userId = currentUserService.getCurrentUser().getId();
        List<OrderDto> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
        log.info("Fetched {} orders for userId={}", orders.size(), userId);
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderDetails(String orderNumber) {
        return mapToDto(getOrderEntity(orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderEntity(String orderNumber) {
        log.info("Fetching order entity for orderNumber={}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
        if (!order.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new AppException("You are not allowed to view this order.");
        }
        return order;
    }

    @Override
    @Transactional
    public void markOrderPaid(String orderNumber, String paymentReference) {
        Order order = getOrderEntity(orderNumber);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PROCESSING);
        order.setPaymentReference(paymentReference);
    }

    @Override
    @Transactional
    public OrderDto updateOrder(String orderNumber, OrderDto updatedDto) {
        Order order = getOrderEntity(orderNumber);

        if (updatedDto.getStatus() != null && !updatedDto.getStatus().isBlank()) {
            order.setStatus(parseOrderStatus(updatedDto.getStatus()));
        }
        if (updatedDto.getPaymentStatus() != null && !updatedDto.getPaymentStatus().isBlank()) {
            order.setPaymentStatus(parsePaymentStatus(updatedDto.getPaymentStatus()));
        }
        order.setPaymentReference(updatedDto.getPaymentReference());

        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(String orderNumber) {
        Order order = getOrderEntity(orderNumber);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return mapToDto(order);
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException("Delivered orders cannot be cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        log.info("Order {} cancelled successfully", orderNumber);
        return mapToDto(orderRepository.save(order));
    }

    private OrderDto mapToDto(Order order) {
        AddressDto addressDto = null;
        if (order.getAddress() != null) {
            addressDto = AddressDto.builder()
                    .id(order.getAddress().getId())
                    .userId(order.getAddress().getUser().getId())
                    .fullName(order.getAddress().getFullName())
                    .phoneNumber(order.getAddress().getPhoneNumber())
                    .addressLine1(order.getAddress().getAddressLine1())
                    .addressLine2(order.getAddress().getAddressLine2())
                    .city(order.getAddress().getCity())
                    .state(order.getAddress().getState())
                    .pincode(order.getAddress().getPincode())
                    .latitude(order.getAddress().getLatitude())
                    .longitude(order.getAddress().getLongitude())
                    .isDefault(order.getAddress().isDefault())
                    .build();
        }

        return OrderDto.builder()
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentReference(order.getPaymentReference())
                .createdAt(order.getCreatedAt())
                .address(addressDto)
                .items(order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .productName(item.getProductName())
                                .productImageUrl(item.getProductImageUrl())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQuantity())
                                .totalPrice(item.getTotalPrice())
                                .build())
                        .toList())
                .build();
    }

    private OrderStatus parseOrderStatus(String value) {
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new AppException("Invalid order status: " + value);
        }
    }

    private PaymentStatus parsePaymentStatus(String value) {
        try {
            return PaymentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new AppException("Invalid payment status: " + value);
        }
    }
}
