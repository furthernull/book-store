package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookSearchParametersDto;
import java.util.List;

public interface BookService {
    BookDto save(BookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    List<BookDto> search(BookSearchParametersDto searchParameters);

    BookDto updateBookById(Long id, BookRequestDto requestDto);

    void delete(Long id);
}
