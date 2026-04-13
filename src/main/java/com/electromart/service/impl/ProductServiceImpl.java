package com.electromart.service.impl;

import com.electromart.dto.ProductDto;
import com.electromart.entity.Product;
import com.electromart.repository.ProductRepository;
import com.electromart.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDto> getAllProducts(String categorySlug) {
        List<Product> products = (categorySlug == null || categorySlug.isBlank())
                ? productRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc()
                : productRepository.findByCategorySlugAndActiveTrueOrderByFeaturedDescCreatedAtDesc(categorySlug);
        return products.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findTop6ByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .featured(product.isFeatured())
                .categoryName(product.getCategory().getName())
                .categorySlug(product.getCategory().getSlug())
                .build();
    }
}
