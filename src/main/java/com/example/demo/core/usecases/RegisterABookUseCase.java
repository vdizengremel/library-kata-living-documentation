package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookId;
import com.example.demo.core.domain.book.BookRepository;

public class RegisterABookUseCase {
    private final BookRepository bookRepository;

    public RegisterABookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void execute(RegisterABookCommand command) {
        BookId bookId = bookRepository.generateNewId();
        Book newBook = new Book(bookId);
        bookRepository.add(newBook);
    }

    static interface RegisterABookCommand {
        String getIsbn();
        String getTitle();
        String getAuthor();
    }

    static interface RegisterABookPresenter<T> {
        T presentRegistrationSuccess();
        T presentBoolAlreadyRegistered();
    }
}
