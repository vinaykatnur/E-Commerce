package com.electromart.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartDto {
    private Long cartId;
    private List<CartItemDto> items;
    private BigDecimal subtotal;
    private int totalItems;
}
