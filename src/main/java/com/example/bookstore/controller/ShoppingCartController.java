package com.example.bookstore.controller;

import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.dto.cartitem.CartItemUpdateQuantityDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "endpoints for managing cart items")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Validated
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add book", description = "add book to the shopping cart")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ShoppingCartDto addBook(Authentication authentication,
                               @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addToShoppingCart(authentication, requestDto);
    }

    @Operation(summary = "Fetch shopping cart", description = "fetch shopping cart of the user")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication);
    }

    @Operation(summary = "Update cart item", description = "update existing item in shopping cart")
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto update(@PathVariable Long cartItemId,
            @RequestBody @Valid CartItemUpdateQuantityDto requestDto,
                              Authentication authentication) {
        return shoppingCartService.updateShoppingCart(authentication, cartItemId, requestDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/items/{cartItemId}")
    public void delete(@PathVariable @Positive Long cartItemId,
                       Authentication authentication) {
        shoppingCartService.deleteItem(cartItemId, authentication);
    }
}
