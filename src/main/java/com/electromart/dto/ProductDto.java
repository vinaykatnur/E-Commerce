package com.electromart.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private boolean featured;
    private String categoryName;
    private String categorySlug;
}
