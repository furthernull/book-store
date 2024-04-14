package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookRequestDto;
import com.example.bookstore.model.Book;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    List<BookDto> map(Collection<Book> books);

    Book toModel(BookRequestDto requestDto);

    @Mapping(target = "title", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "author", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isbn", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "price", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "coverImage", nullValuePropertyMappingStrategy
            = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(@MappingTarget Book book, BookRequestDto requestDto);
}
