package com.electromart.repository;

import com.electromart.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"cart", "cart.items", "cart.items.product"})
    Optional<User> findWithCartByEmailIgnoreCase(String email);
}
