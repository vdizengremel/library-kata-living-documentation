package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!inMemoryRepository")
public class BookAdapter implements BookRepository {
    @Override
    public Optional<Book> findByIsbn(ISBN isbn) {
        return Optional.empty();
    }

    @Override
    public void register(Book newBook) {

    }
}
