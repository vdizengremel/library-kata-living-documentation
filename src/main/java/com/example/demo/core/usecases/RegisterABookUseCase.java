package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.book.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterABookUseCase {
    private final BookRepository bookRepository;

    public RegisterABookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public <T> T execute(RegisterABookCommand command, RegisterABookPresenter<T> presenter) {
        ISBN isbn = new ISBN(command.getIsbn());

        Optional<Book> foundBook = this.bookRepository.findByIsbn(isbn);
        if(foundBook.isPresent()) {
            return presenter.presentBookAlreadyRegistered();
        }

        Book newBook = new Book(isbn, command.getTitle(), command.getAuthor());
        bookRepository.register(newBook);
        return presenter.presentRegistrationSuccess();
    }

    public interface RegisterABookCommand {
        String getIsbn();
        String getTitle();
        String getAuthor();
    }

    public interface RegisterABookPresenter<T> {
        T presentRegistrationSuccess();
        T presentBookAlreadyRegistered();
    }
}
