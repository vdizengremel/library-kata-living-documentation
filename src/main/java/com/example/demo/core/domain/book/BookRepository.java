package com.example.demo.core.domain.book;

import java.util.Optional;

public interface BookRepository {
    BookId generateNewId();
    Optional<Book> findById(BookId id);

    void add(Book newBook);
}
