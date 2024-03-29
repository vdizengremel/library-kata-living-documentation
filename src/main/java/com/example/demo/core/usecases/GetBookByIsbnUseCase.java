package com.example.demo.core.usecases;

import com.example.living.documentation.annotation.UseCase;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;

@UseCase
public class GetBookByIsbnUseCase {
    private final BookRepository bookRepository;

    public GetBookByIsbnUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public <T> T execute(GetBookByIsbnQuery query, GetBookByIsbnPresenter<T> presenter) {
        var optionalBook = bookRepository.findByIsbn(new ISBN(query.getIsbn()));
        return optionalBook.map(presenter::presentBook).orElseGet(presenter::presentBookNotFound);
    }

    public interface GetBookByIsbnQuery {
        String getIsbn();
    }

    public interface GetBookByIsbnPresenter<T> {
        T presentBook(Book book);
        T presentBookNotFound();
    }
}
