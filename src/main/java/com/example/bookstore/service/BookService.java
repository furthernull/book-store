package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.dto.BookSearchParametersDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(BookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    List<BookDto> search(BookSearchParametersDto searchParameters);

    BookDto updateBookById(Long id, BookRequestDto requestDto);

    void delete(Long id);
}
