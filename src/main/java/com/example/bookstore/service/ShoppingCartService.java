package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.CartItemUpdateQuantityDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto addToShoppingCart(Authentication authentication,
                                      CartItemRequestDto requestDto);

    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCart(Authentication authentication);

    ShoppingCartDto updateShoppingCart(Authentication authentication,
                                       Long cartItemId,
                                       CartItemUpdateQuantityDto requestDto);

    void deleteItem(Long cartItemId, Authentication authentication);
}
