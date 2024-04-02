package com.example.stepdefinitions;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.usecases.GetBookByIsbnUseCase;
import com.example.demo.core.usecases.RegisterABookUseCase;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class BookStepDefinitions {
    private final BookInMemoryRepository bookInMemoryRepository;

    private PresenterException thrownException;
    private Book returnedBook;

    public BookStepDefinitions(World world) {
        this.bookInMemoryRepository = new BookInMemoryRepository();
        world.bookInMemoryRepository = this.bookInMemoryRepository;
    }

    @Given("already registered books:")
    public void alreadyRegisteredBooks(List<Map<String, String>> registeredBooks) {
        registeredBooks.forEach(this::registerBook);
    }

    @When("registering new book:")
    public void registeringNewBook(List<Map<String, String>> registeredBook) {
        this.thrownException = catchPresenterException(() -> registerBook(registeredBook.getFirst()));
    }

    @Then("this book should be registered:")
    public void thisBookShouldBeRegistered(List<Map<String, String>> expected) {
        var expectedBookData = expected.getFirst();
        ISBN isbn = new ISBN(expectedBookData.get("isbn"));

        Optional<Book> optionalBook = bookInMemoryRepository.findByIsbn(isbn);
        assertThat(optionalBook).isPresent();

        Book expectedBook = new Book(isbn, expectedBookData.get("title"), expectedBookData.get("author"));
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @Then("the book registration should fail because {}")
    public void theBookRegistrationResultShouldBeAnErrorIndicatingABookWithSameISBNIsAlreadyRegistered(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    private void registerBook(Map<String, String> bookData) {
        var useCase = new RegisterABookUseCase(bookInMemoryRepository);
        var command = RegisterABookCommand.builder()
                .isbn(bookData.get("isbn"))
                .title(bookData.get("title"))
                .author(bookData.get("author"))
                .build();

        useCase.execute(command, new RegisterABookPresenterForTest());
    }

    @When("getting book with ISBN {}")
    public void gettingBookWithISBN(String isbn) {
        var useCase = new GetBookByIsbnUseCase(bookInMemoryRepository);
        this.thrownException = catchPresenterException(() -> this.returnedBook = useCase.execute(() -> isbn, new GetBookByIsbnPresenterForTest()));
    }

    @Then("the following book should be returned:")
    public void theFollowingBookShouldBeReturned(List<Map<String, String>> expected) {
        Map<String, String> bookData = expected.getFirst();
        assertThat(returnedBook).usingRecursiveComparison().isEqualTo(new Book(new ISBN(bookData.get("isbn")), bookData.get("title"), bookData.get("author")));
    }

    private PresenterException catchPresenterException(Runnable fn) {
        try {
            fn.run();
            return null;
        } catch (PresenterException presenterException) {
            return presenterException;
        }
    }

    @Then("the returning of book should fail because {}")
    public void theReturningOfBookShouldFailBecauseBookDoesNotExist(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    @Getter
    @Builder
    static class RegisterABookCommand implements RegisterABookUseCase.RegisterABookCommand {
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
        public String presentBookAlreadyRegistered() {
            throw new PresenterException("a book with same ISBN is already registered");
        }
    }

    static class GetBookByIsbnPresenterForTest implements GetBookByIsbnUseCase.GetBookByIsbnPresenter<Book> {

        @Override
        public Book presentBook(Book book) {
            return book;
        }

        @Override
        public Book presentBookNotFound() {
            throw new PresenterException("book does not exist");
        }
    }
}
