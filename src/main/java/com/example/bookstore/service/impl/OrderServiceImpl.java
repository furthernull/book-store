package com.example.bookstore.service.impl;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderUpdateRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.order.OrderRepository;
import com.example.bookstore.repository.orderitem.OrderItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartServiceImpl shoppingCartService;

    @Transactional
    @Override
    public OrderDto createOrder(User user, OrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Can't find shopping cart for user: %s", user.getEmail())
                ));
        Order order = orderMapper.initOrder(shoppingCart, requestDto);
        shoppingCartService.clearShoppingCart(user.getId());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> getOrderHistory(Long userId, Pageable pageable) {
        return orderMapper.toDto(orderRepository.findOrderByUserId(userId, pageable));
    }

    @Transactional
    @Override
    public OrderDto updateOrder(Long id, OrderUpdateRequestDto requestDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Unable to update order by id: " + id)
        );
        orderMapper.update(order, requestDto);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> getOrderItemsBySpecificOrder(
            Long userId,
            Long orderId,
            Pageable pageable) {
        List<OrderItem> orderItems = orderItemRepository
                .findAllOrderItemsByOrderIdAndUserId(userId, orderId, pageable);
        return orderItemMapper.toDto(orderItems);
    }

    @Override
    public OrderItemDto getSpecificOrderItem(Long userId, Long orderId, Long id) {
        OrderItem orderItem = orderItemRepository
                .findOrderItemByOrderIdAndUserIdAndAndOrderItemId(userId, orderId, id);
        return orderItemMapper.toDto(orderItem);
    }
}
