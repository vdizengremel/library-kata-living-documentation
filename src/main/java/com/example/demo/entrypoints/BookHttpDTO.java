package com.example.demo.entrypoints;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.usecases.RegisterABookUseCase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookHttpDTO implements RegisterABookUseCase.RegisterABookCommand, Book.BookInterest {
    public String isbn;
    public String title;
    public String author;
}
