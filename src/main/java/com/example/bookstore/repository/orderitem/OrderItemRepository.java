package com.example.bookstore.repository.orderitem;

import com.example.bookstore.model.OrderItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT item FROM OrderItem item "
            + "JOIN FETCH item.order order "
            + "JOIN FETCH order.user user "
            + "WHERE order.id = :orderId AND user.id = :userId")
    List<OrderItem> findAllOrderItemsByOrderIdAndUserId(
            Long userId,
            Long orderId,
            Pageable pageable
    );

    @Query("SELECT item FROM OrderItem item "
            + "JOIN FETCH item.order order "
            + "JOIN FETCH order.user user "
            + "WHERE user.id = :userId AND order.id = :orderId AND item.id = :id")
    OrderItem findOrderItemByOrderIdAndUserIdAndAndOrderItemId(
            Long userId,
            Long orderId,
            Long id
    );
}
