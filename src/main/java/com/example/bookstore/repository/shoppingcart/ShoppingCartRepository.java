package com.example.bookstore.repository.shoppingcart;

import com.example.bookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT sc FROM ShoppingCart sc JOIN FETCH sc.user "
            + "JOIN FETCH sc.cartItems WHERE sc.id = :id")
    Optional<ShoppingCart> findById(Long id);
}
