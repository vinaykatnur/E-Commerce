package com.electromart.repository;

import com.electromart.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "address"})
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"items", "user", "address"})
    Optional<Order> findByOrderNumber(String orderNumber);

    boolean existsByAddressIdAndUserId(Long addressId, Long userId);

    void deleteByOrderNumber(String orderNumber);
}
