package com.electromart.service;

import com.electromart.dto.ProductDto;
import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts(String categorySlug);

    List<ProductDto> getFeaturedProducts();
}
