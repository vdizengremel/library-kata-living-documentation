package com.example.demo.core.domain.book;

import lombok.Getter;

/**
 * Book to be borrowed.
 */
public class Book {
    @Getter
    private final ISBN isbn;
    private final String title;
    private final String author;

    public Book(ISBN isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public void provideInterest(BookInterest interest) {
        interest.setIsbn(isbn.value());
        interest.setTitle(title);
        interest.setAuthor(author);
    }

    public interface BookInterest {
        void setIsbn(String isbn);
        void setTitle(String title);
        void setAuthor(String author);
    }
}
