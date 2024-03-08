package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.Book;
import lombok.Setter;

@Setter
public class BookMongoDTO implements Book.BookInterest {
    public String id;
    public String title;
    public String author;

    @Override
    public void setIsbn(String isbn) {
        this.id = isbn;
    }
}
