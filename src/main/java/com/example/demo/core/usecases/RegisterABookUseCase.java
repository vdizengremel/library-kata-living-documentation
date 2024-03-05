package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.book.BookRepository;

public class RegisterABookUseCase {
    private final BookRepository bookRepository;

    public RegisterABookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void execute(RegisterABookCommand command) {
        ISBN ISBN = new ISBN(command.getIsbn());
        Book newBook = new Book(ISBN, command.getTitle(), command.getAuthor());
        bookRepository.add(newBook);
    }

    public interface RegisterABookCommand {
        String getIsbn();
        String getTitle();
        String getAuthor();
    }

    public interface RegisterABookPresenter<T> {
        T presentRegistrationSuccess();
        T presentBoolAlreadyRegistered();
    }
}
