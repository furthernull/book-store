package com.example.bookstore.service.impl;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CartItemRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public CartItemDto addToCart(CartItemRequestDto requestDto, Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ShoppingCart shoppingCart = shoppingCartRepository.getReferenceById(userId);
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        Book book = bookRepository.findById(requestDto.bookId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find Book by id: "
                        + requestDto.bookId()));
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemDto updateCartItem(Long cartItemId,
                                      CartItemRequestDto requestDto,
                                      Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't retrieve CartItem by id's"));
        cartItemMapper.update(cartItem, requestDto);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteItem(Long cartItemId, Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't retrieve CartItem"));
        cartItemRepository.delete(cartItem);
    }
}
