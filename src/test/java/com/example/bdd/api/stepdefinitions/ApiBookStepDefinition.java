package com.example.bdd.api.stepdefinitions;

import com.example.bdd.BookData;
import com.example.bdd.feature.stepdefinitions.BookStepDefinitions;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.usecases.GetBookByIsbnUseCase;
import com.example.demo.core.usecases.RegisterABookUseCase;
import io.cucumber.java.en.Given;
import org.mockito.Mockito;

public class ApiBookStepDefinition {
    private final RegisterABookUseCase registerABookUseCase;
    private final GetBookByIsbnUseCase getBookByIsbnUseCase;

    public ApiBookStepDefinition(RegisterABookUseCase registerABookUseCase, GetBookByIsbnUseCase getBookByIsbnUseCase) {
        this.registerABookUseCase = registerABookUseCase;
        this.getBookByIsbnUseCase = getBookByIsbnUseCase;
    }

    @Given("book is not registered yet")
    public void bookIsNotRegistered() {
        Mockito.when(registerABookUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, RegisterABookUseCase.RegisterABookPresenter.class);
            return argument.presentRegistrationSuccess();
        });

        Mockito.when(getBookByIsbnUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, GetBookByIsbnUseCase.GetBookByIsbnPresenter.class);
            return argument.presentBookNotFound();
        });
    }

    @Given("book with same ISBN already exists")
    public void bookWithSameISBNAlreadyExists() {
        Mockito.when(registerABookUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, RegisterABookUseCase.RegisterABookPresenter.class);
            return argument.presentBookAlreadyRegistered();
        });
    }

    @Given("book {} is registered")
    public void bookExists(String bookName) {
        BookData bookData = new BookData();
        var registerABookCommandByBookName = bookData.getRegisterABookCommandByBookName(bookName);
        Mockito.when(getBookByIsbnUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, GetBookByIsbnUseCase.GetBookByIsbnPresenter.class);
            return argument.presentBook(new Book(new ISBN(registerABookCommandByBookName.getIsbn()), registerABookCommandByBookName.getTitle(), registerABookCommandByBookName.getAuthor()));
        });
    }
}
