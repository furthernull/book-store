package com.example.bookstore.repository.book;

import com.example.bookstore.exception.SpecificationProviderNotFoundException;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import com.example.bookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderNotFoundException(
                        "Can't find correct specification provider for key " + key)
                );
    }
}
