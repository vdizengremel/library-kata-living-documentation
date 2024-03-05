package com.example.demo.core.domain.book;

import lombok.Getter;

public class Book {
    @Getter
    private final ISBN isbn;
    private String title;
    private String author;

    public Book(ISBN isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public void provideInterest(BookInterest interest) {
        interest.setId(isbn.value());
        interest.setTitle(title);
        interest.setAuthor(author);
    }

    public interface BookInterest {
        void setId(String id);
        void setTitle(String title);
        void setAuthor(String author);
    }
}
