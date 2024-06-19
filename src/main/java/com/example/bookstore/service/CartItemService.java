package com.example.bookstore.service;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import org.springframework.security.core.Authentication;

public interface CartItemService {

    CartItemDto addToCart(CartItemRequestDto requestDto,
                          Authentication authentication);

    CartItemDto updateCartItem(Long cartItemId,
                               CartItemRequestDto requestDto,
                               Authentication authentication
    );

    void deleteItem(Long cartItemId, Authentication authentication);
}
