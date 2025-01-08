package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookRequestDto;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.book.BookSpecificationBuilder;
import com.example.bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    private static final Long ID = 1L;
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    
    @InjectMocks
    private BookServiceImpl bookService;
    private BookRequestDto bookRequestDto;
    private BookRequestDto updateBookRequestDto;
    private Book book;
    private Book updatedBook;
    private BookDto bookResponseDto;
    private BookDto updatedBookResponseDto;
    private BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    private Category category;

    @Test
    @DisplayName("Verify save() method with valid requestDto")
    void save_ValidCreateProductRequestDto_ReturnValidBookDto() {
        when(bookMapper.toModel(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        BookDto actual = bookService.save(bookRequestDto);

        assertEquals(actual, bookResponseDto);
        verify(bookMapper).toModel(bookRequestDto);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAll() method should return valid list")
    void findAll_ReturnValidListOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookPage)).thenReturn(List.of(bookResponseDto));

        List<BookDto> actualList = bookService.findAll(pageable);
        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0), bookResponseDto);

        verify(bookRepository, Mockito.times(1)).findAll(pageable);
        verify(bookMapper).toDto(bookPage);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAllByCategoryId() method")
    void findAllByCategoryId_ReturnValidListOfBookDtoWithoutCategoryIds() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookDtoWithoutCategoryIds> books = List.of(bookDtoWithoutCategoryIds);

        when(bookRepository.findAllByCategoryId(ID, pageable)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(List.of(book))).thenReturn(books);

        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(ID, pageable);

        assertEquals(actual.size(), 1);
        assertEquals(actual.get(0), bookDtoWithoutCategoryIds);

        verify(bookRepository, Mockito.times(1)).findAllByCategoryId(ID, pageable);
        verify(bookMapper, Mockito.times(1)).toDtoWithoutCategories(List.of(book));
        verify(bookMapper).toDtoWithoutCategories(List.of(book));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findById() method should return valid list")
    void findById_ValidBookId_ReturnValidBookDto() {
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookResponseDto);

        BookDto actual = bookService.findById(ID);

        assertEquals(actual, bookResponseDto);
        verify(bookRepository, Mockito.times(1)).findById(ID);
        verify(bookMapper, Mockito.times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findById() method with not valid id")
    void findById_InvalidBookId_ShouldThrowException() {
        Long id = 100L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(id));

        String expected = "Can't find book by id: " + id;
        assertEquals(expected, ex.getMessage());
    }

    @Test
    @DisplayName("Verify updateBookById() method with valid request")
    void updateBookById_ValidBookRequestDto_ReturnValidBookDto() {
        when(bookRepository.findById(ID)).thenReturn(Optional.of(book));
        when(bookService.updateBookById(ID, bookRequestDto)).thenReturn(bookResponseDto);

        BookDto updatedBookDto = bookService.updateBookById(ID, bookRequestDto);

        assertEquals(updatedBookDto, bookResponseDto);
    }

    @Test
    @DisplayName("Verify updateBookById() method with not valid id")
    void updateBookById_NotValidId_ShouldThrowException() {
        Long id = -100L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookById(id, bookRequestDto));

        String expected = "Can't find book by id: " + id;
        assertEquals(expected, ex.getMessage());
    }

    @Test
    @DisplayName("Verify search() method with valid param")
    void search_ValidSearchParams_ReturnValidBookDtoList() {
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(null, null);
        Specification<Book> specification = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books);

        when(bookSpecificationBuilder.build(searchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(bookPage)).thenReturn(List.of(bookResponseDto));

        List<BookDto> bookDtos = bookService.search(searchParameters, pageable);
        assertEquals(bookDtos.size(), 1);
        assertEquals(bookDtos.get(0), bookResponseDto);
    }

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(ID);
        category.setName("category");
        category.setDescription("category description");

        bookRequestDto = new BookRequestDto();
        bookRequestDto.setTitle("Title");
        bookRequestDto.setAuthor("Author");
        bookRequestDto.setIsbn("123-1234567890");
        bookRequestDto.setPrice(BigDecimal.valueOf(10L));
        bookRequestDto.setDescription("Description");
        bookRequestDto.setCoverImage("Cover Image");
        bookRequestDto.setCategoryIds(Set.of(ID));

        updateBookRequestDto = new BookRequestDto();
        updateBookRequestDto.setTitle("Updated title");
        updateBookRequestDto.setAuthor("Updated author");
        updateBookRequestDto.setIsbn("890-7654321321");
        updateBookRequestDto.setPrice(BigDecimal.valueOf(14.99));
        updateBookRequestDto.setDescription("Updated description");
        updateBookRequestDto.setCoverImage("Updated cover image");
        updateBookRequestDto.setCategoryIds(Set.of(ID));

        book = new Book();
        book.setId(ID);
        book.setTitle(bookRequestDto.getTitle());
        book.setAuthor(bookRequestDto.getAuthor());
        book.setIsbn(bookRequestDto.getIsbn());
        book.setPrice(bookRequestDto.getPrice());
        book.setDescription(bookRequestDto.getDescription());
        book.setCoverImage(bookRequestDto.getCoverImage());
        book.setCategories(Set.of(category));

        updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.setTitle(updateBookRequestDto.getTitle());
        updatedBook.setAuthor(updateBookRequestDto.getAuthor());
        updatedBook.setIsbn(updateBookRequestDto.getIsbn());
        updatedBook.setPrice(updateBookRequestDto.getPrice());
        updatedBook.setDescription(updateBookRequestDto.getDescription());
        updatedBook.setCoverImage(updateBookRequestDto.getCoverImage());
        updatedBook.setCategories(Set.of(category));

        bookResponseDto = new BookDto();
        bookResponseDto.setId(book.getId());
        bookResponseDto.setTitle(book.getTitle());
        bookResponseDto.setAuthor(book.getAuthor());
        bookResponseDto.setIsbn(book.getIsbn());
        bookResponseDto.setPrice(book.getPrice());
        bookResponseDto.setDescription(book.getDescription());
        bookResponseDto.setCoverImage(book.getCoverImage());
        bookResponseDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));

        updatedBookResponseDto = new BookDto();
        updatedBookResponseDto.setId(updatedBook.getId());
        updatedBookResponseDto.setTitle(updatedBook.getTitle());
        updatedBookResponseDto.setAuthor(updatedBook.getAuthor());
        updatedBookResponseDto.setIsbn(updatedBook.getIsbn());
        updatedBookResponseDto.setPrice(updatedBook.getPrice());
        updatedBookResponseDto.setDescription(updatedBook.getDescription());
        updatedBookResponseDto.setCoverImage(updatedBook.getCoverImage());
        updatedBookResponseDto.setCategoryIds(updatedBook.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));

        bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(book.getId());
        bookDtoWithoutCategoryIds.setTitle(book.getTitle());
        bookDtoWithoutCategoryIds.setAuthor(book.getAuthor());
        bookDtoWithoutCategoryIds.setIsbn(book.getIsbn());
        bookDtoWithoutCategoryIds.setPrice(book.getPrice());
        bookDtoWithoutCategoryIds.setDescription(book.getDescription());
        bookDtoWithoutCategoryIds.setCoverImage(book.getCoverImage());
    }
}
