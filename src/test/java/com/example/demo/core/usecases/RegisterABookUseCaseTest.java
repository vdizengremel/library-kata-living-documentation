package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.infrastructure.BookInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterABookUseCaseTest {
    private RegisterABookUseCase registerABookUseCase;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository = new BookInMemoryRepository();
        registerABookUseCase = new RegisterABookUseCase(bookRepository);
    }

    @Test
    void shouldRegisterBook_whenNotRegisteredYet() {
        var command = Command.builder().isbn("isbn").title("title").author("author").build();
        registerABookUseCase.execute(command, new RegisterABookPresenterForTest());

        Optional<Book> optionalBook = bookRepository.findByIsbn(new ISBN("isbn"));
        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(new Book(new ISBN("isbn"), "title", "author"));
    }

    @Test
    void shouldPresentErrorWithoutUpdatingBook_whenBookAlreadyRegistered() {
        var command = Command.builder().isbn("isbn").title("title").author("author").build();
        registerABookUseCase.execute(command, new RegisterABookPresenterForTest());

        var secondCommand = Command.builder().isbn("isbn").title("another title").author("another author").build();
        assertThatThrownBy(() -> registerABookUseCase.execute(secondCommand, new RegisterABookPresenterForTest())).hasMessage("Book already registered");

        Optional<Book> optionalBook = bookRepository.findByIsbn(new ISBN("isbn"));
        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(new Book(new ISBN("isbn"), "title", "author"));
    }

    @Getter
    @Builder
    static class Command implements RegisterABookUseCase.RegisterABookCommand {
        String isbn;
        String title;
        String author;
    }

    static class RegisterABookPresenterForTest implements RegisterABookUseCase.RegisterABookPresenter<String> {

        @Override
        public String presentRegistrationSuccess() {
            return "success";
        }

        @Override
        public String presentBoolAlreadyRegistered() {
            throw new RuntimeException("Book already registered");
        }
    }

}
