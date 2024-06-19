package com.example.bookstore.service;

import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.User;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {

    ShoppingCartDto createShoppingCart(User user);

    ShoppingCartDto getShoppingCart(Authentication authentication);
}
