package com.example.stepdefinitions;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.usecases.GetBookByIsbnUseCase;
import com.example.demo.core.usecases.RegisterABookUseCase;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import com.example.test.BookData;
import com.example.test.PresenterException;
import com.example.test.World;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.test.CucumberUtils.catchPresenterException;
import static org.assertj.core.api.Assertions.assertThat;

public class BookStepDefinitions {
    private final BookInMemoryRepository bookInMemoryRepository;

    private PresenterException thrownException;
    private Book returnedBook;
    private final RegisterABookUseCase registerABookUseCase;
    private final BookData bookData;


    public BookStepDefinitions(World world) {
        this.bookInMemoryRepository = world.bookInMemoryRepository;
        registerABookUseCase = new RegisterABookUseCase(bookInMemoryRepository);
        bookData = world.bookData;
    }

    @Given("already registered books:")
    public void alreadyRegisteredBooks(List<String> registeredBooks) {
        registeredBooks.stream().map(bookData::getRegisterABookCommandByBookName).forEach(this::registerBook);
    }

    @When("registering the book {}")
    public void registeringTheBook(String bookName) {
        var command = bookData.getRegisterABookCommandByBookName(bookName);
        this.thrownException = catchPresenterException(() -> registerBook(command));
    }

    @Then("this book should be registered:")
    public void thisBookShouldBeRegistered(List<Book> expected) {
        var expectedBook = expected.getFirst();
        ISBN isbn = expectedBook.getIsbn();

        Optional<Book> optionalBook = bookInMemoryRepository.findByIsbn(isbn);
        assertThat(optionalBook).isPresent();
        assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @Then("the book registration should fail because {}")
    public void theBookRegistrationResultShouldBeAnErrorIndicatingABookWithSameISBNIsAlreadyRegistered(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    private void registerBook(RegisterABookCommand command) {
        registerABookUseCase.execute(command, new RegisterABookPresenterForTest());
    }

    @When("getting book with ISBN {}")
    public void gettingBookWithISBN(String isbn) {
        var useCase = new GetBookByIsbnUseCase(bookInMemoryRepository);
        this.thrownException = catchPresenterException(() -> this.returnedBook = useCase.execute(() -> isbn, new GetBookByIsbnPresenterForTest()));
    }

    @Then("the following book should be returned:")
    public void theFollowingBookShouldBeReturned(List<Book> expected) {
        Book expectedBook = expected.getFirst();
        assertThat(returnedBook).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DataTableType
    public Book mapRowToBook(Map<String, String> bookData) {
        return new Book(new ISBN(bookData.get("isbn")), bookData.get("title"), bookData.get("author"));
    }

    @Then("the returning of book should fail because {}")
    public void theReturningOfBookShouldFailBecauseBookDoesNotExist(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    @Getter
    @Builder
    public static class RegisterABookCommand implements RegisterABookUseCase.RegisterABookCommand {
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
