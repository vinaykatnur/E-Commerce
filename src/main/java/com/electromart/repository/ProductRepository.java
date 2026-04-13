package com.electromart.repository;

import com.electromart.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "category")
    List<Product> findByActiveTrueOrderByFeaturedDescCreatedAtDesc();

    @EntityGraph(attributePaths = "category")
    List<Product> findByCategorySlugAndActiveTrueOrderByFeaturedDescCreatedAtDesc(String categorySlug);

    @EntityGraph(attributePaths = "category")
    List<Product> findTop6ByActiveTrueAndFeaturedTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndActiveTrue(Long id);
}
