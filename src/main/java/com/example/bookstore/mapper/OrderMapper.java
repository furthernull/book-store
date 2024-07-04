package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderRequestDto;
import com.example.bookstore.dto.order.OrderUpdateRequestDto;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.ShoppingCart;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    void update(@MappingTarget Order order, OrderUpdateRequestDto requestDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    OrderDto toDto(Order order);

    List<OrderDto> toDto(List<Order> orders);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderDate", expression = "java(getCurrentDate())")
    @Mapping(target = "status", expression = "java(getDefaultStatus())")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "total", source = "shoppingCart", qualifiedByName = "totalPrice")
    @Mapping(target = "shippingAddress", source = "requestDto.shippingAddress")
    @Mapping(target = "orderItems", source = "shoppingCart.cartItems")
    Order initOrder(ShoppingCart shoppingCart, OrderRequestDto requestDto);

    default LocalDateTime getCurrentDate() {
        return LocalDateTime.now();
    }

    default Order.Status getDefaultStatus() {
        return Order.Status.PROCESSING;
    }

    @AfterMapping
    default void setOrderToItem(Order order) {
        order.getOrderItems()
                .stream()
                .forEach(i -> i.setOrder(order));
    }

    @Named("totalPrice")
    default BigDecimal getTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems()
                .stream()
                .map(item -> item.getBook()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("statusToString")
    default String getStatusString(Order.Status status) {
        return status.toString();
    }

    @Named("stringToStatus")
    default Order.Status getStatus(String status) {
        return Order.Status.valueOf(status);
    }
}
