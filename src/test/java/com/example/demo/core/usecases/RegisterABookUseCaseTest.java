package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookId;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.infrastructure.BookInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterABookUseCaseTest {
    private RegisterABookUseCase registerABookUseCase;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp(){
        bookRepository = new BookInMemoryRepository();
        registerABookUseCase = new RegisterABookUseCase(bookRepository);
    }

    @Test
    void shouldRegisterBook(){
        var command = Command.builder().isbn("isbn").title("title").author("author").build();
        registerABookUseCase.execute(command);

        Optional<Book> optionalBook = bookRepository.findById(BookInMemoryRepository.BOOK_IDS.getFirst());
        assertThat(optionalBook).isPresent();
    }

    @Getter
    @Builder
    static class Command implements RegisterABookUseCase.RegisterABookCommand {
        String isbn;
        String title;
        String author;
    }

}
