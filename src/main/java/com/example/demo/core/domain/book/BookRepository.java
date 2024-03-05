package com.example.demo.core.domain.book;

import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(ISBN id);

    void add(Book newBook);
}
