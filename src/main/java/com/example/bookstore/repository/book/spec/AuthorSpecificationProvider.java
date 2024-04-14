package com.example.bookstore.repository.book.spec;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String ATTRIBUTE_NAME = "author";

    @Override
    public String getKey() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(ATTRIBUTE_NAME).in(Arrays
                .stream(params)
                .toArray()
        );
    }
}
