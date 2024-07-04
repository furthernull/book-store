package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDto(Iterable<OrderItem> orderItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "cartItem", qualifiedByName = "price")
    OrderItem cartToOrder(CartItem cartItem);

    @Named("price")
    default BigDecimal getPrice(CartItem cartItem) {
        return cartItem.getBook().getPrice();
    }
}
