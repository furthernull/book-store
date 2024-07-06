package com.example.bookstore.controller;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderUpdateRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "endpoints for managing orders")
@RequiredArgsConstructor
@RequestMapping("/orders")
@RestController
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place an order",
            description = "create an order based on shopping cart")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public OrderDto placeOrder(@AuthenticationPrincipal User user,
                               @RequestBody @Valid OrderRequestDto requestDto
    ) {
        return orderService.createOrder(user, requestDto);
    }

    @Operation(summary = "Get order history",
            description = "get order history related to user")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public List<OrderDto> getOrderHistory(
            @AuthenticationPrincipal User user,
            @ParameterObject Pageable pageable
    ) {
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @Operation(summary = "Get order details",
            description = "retrieve all order items for specific order")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getOrderDetails(@AuthenticationPrincipal User user,
                                              @PathVariable Long orderId,
                                              @ParameterObject Pageable pageable
    ) {
        return orderService.getOrderItemsBySpecificOrder(user.getId(), orderId, pageable);
    }

    @Operation(summary = "Get specific item",
            description = "retrieve specific order item within an order")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{id}")
    public Object getSpecificItem(@AuthenticationPrincipal User user,
                                  @PathVariable Long orderId,
                                  @PathVariable Long id) {
        return orderService.getSpecificOrderItem(user.getId(), orderId, id);
    }

    @Operation(summary = "Update order status",
            description = "update the status of an order")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                  @RequestBody @Valid OrderUpdateRequestDto requestDto) {
        return orderService.updateOrder(id, requestDto);
    }
}
