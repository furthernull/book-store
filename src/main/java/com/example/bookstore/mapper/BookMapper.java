package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookRequestDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    List<BookDto> toDto(Iterable<Book> bookIterable);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    List<BookDtoWithoutCategoryIds> toDtoWithoutCategories(Iterable<Book> bookIterable);

    @Mapping(target = "categories", ignore = true)
    Book toModel(BookRequestDto requestDto);

    @AfterMapping
    default void setCategories(@MappingTarget Book book, BookRequestDto requestDto) {
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    @Mapping(target = "description", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "coverImage", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categories", ignore = true)
    void updateModel(@MappingTarget Book book, BookRequestDto requestDto);

    @Named("bookById")
    default Book bookById(Long id) {
        return Optional.ofNullable(id)
                .map(Book::new)
                .orElse(null);
    }
}
