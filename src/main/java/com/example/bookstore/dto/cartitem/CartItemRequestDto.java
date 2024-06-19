package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @NotNull
        @Positive
        Long bookId,
        @DecimalMin("1")
        int quantity
) {
}
