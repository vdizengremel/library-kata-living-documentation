package com.example.demo.core.domain.book;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findByIsbn(ISBN isbn);

    void register(Book newBook);
}
