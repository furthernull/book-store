package com.example.bookstore.repository.book;

import com.example.bookstore.dto.BookSearchParametersDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationBuilder;
import com.example.bookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        spec = addSpecificationCriteria(searchParameters.titles(), "title", spec);
        spec = addSpecificationCriteria(searchParameters.authors(), "author", spec);
        return spec;
    }

    private Specification<Book> addSpecificationCriteria(String[] params,
                                                 String key,
                                                 Specification<Book> spec) {
        if (params != null && params.length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(key).getSpecification(params));
        }
        return spec;
    }
}
