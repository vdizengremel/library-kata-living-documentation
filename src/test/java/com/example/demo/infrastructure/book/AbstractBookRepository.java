package com.example.demo.infrastructure.book;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractBookRepository<T extends BookRepository> {
    protected T bookRepository;

    abstract T getBookRepository();

    @AfterEach
    public abstract void deleteAll();

    @BeforeEach
    void setUp() {
        bookRepository = getBookRepository();
    }

    @Test
    void shouldRegisterBook() {
        ISBN isbn = new ISBN("123456");
        Book newBook = new Book(isbn, "Harry Potter", "Rowling");
        bookRepository.register(newBook);

        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);

        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(newBook);
    }

    @Test
    void shouldFindCorrectBookWhenExists() {
        bookRepository.register(new Book(new ISBN("123"), "Harry Potter", "Rowling"));

        bookRepository.register(new Book(new ISBN("456"), "Star Wars", "Lucas"));
        bookRepository.register(new Book(new ISBN("789"), "Game of thrones", "Martin"));

        Optional<Book> optionalBook = bookRepository.findByIsbn(new ISBN("456"));
        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(new Book(new ISBN("456"), "Star Wars", "Lucas"));
    }

    @Test
    void shouldFindReturnEmptyWhenBookDoesNotExist() {
        bookRepository.register(new Book(new ISBN("123"), "Harry Potter", "Rowling"));

        Optional<Book> optionalBook = bookRepository.findByIsbn(new ISBN("456"));
        assertThat(optionalBook).isEmpty();
    }
}
