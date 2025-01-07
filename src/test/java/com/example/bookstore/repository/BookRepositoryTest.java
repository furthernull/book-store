package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find books by id")
    @Sql(scripts = {"classpath:database/add-default-books-to-db.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ValidId_ReturnValidBook() {
        Long bookId = 1L;
        Book book = bookRepository.findById(bookId).get();

        assertNotNull(book);
        assertEquals(book.getId(), bookId);
    }

    @Test
    @DisplayName("Find all books")
    @Sql(scripts = {"classpath:database/add-default-books-to-db.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> books = bookRepository.findAll(pageable);
        Long expectedCount = 2L;

        assertNotNull(books);
        assertEquals(books.getTotalElements(), expectedCount);
    }

    @Test
    @DisplayName("Find all books by category")
    @Sql(scripts = {"classpath:database/add-default-books-to-db.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/delete-all.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnValidBook() {
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Book> allByCategoryId = bookRepository.findAllByCategoryId(categoryId, pageable);

        assertNotNull(allByCategoryId);
        assertEquals(allByCategoryId.size(), 1);
        assertEquals(allByCategoryId.get(0).getId(), categoryId);
    }
}
