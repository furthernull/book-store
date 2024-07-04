package com.example.bookstore.service;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderUpdateRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(User user, OrderRequestDto requestDto);

    List<OrderDto> getOrderHistory(Long userId, Pageable pageable);

    OrderDto updateOrder(Long id, OrderUpdateRequestDto requestDto);

    List<OrderItemDto> getOrderItemsBySpecificOrder(Long userId, Long orderId, Pageable pageable);

    OrderItemDto getSpecificOrderItem(Long userId, Long orderId, Long id);
}
