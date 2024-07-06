package com.example.bookstore.repository.order;

import com.example.bookstore.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems"})
    List<Order> findOrderByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems"})
    Optional<Order> findById(Long id);
}
