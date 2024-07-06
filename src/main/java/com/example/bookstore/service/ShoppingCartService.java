package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.CartItemUpdateQuantityDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto addToShoppingCart(Long userId,
                                      CartItemRequestDto requestDto);

    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto updateShoppingCart(Long userId,
                                       Long cartItemId,
                                       CartItemUpdateQuantityDto requestDto);

    void deleteItem(Long cartItemId, Long userId);

    void clearShoppingCart(Long userId);
}
