package com.example.demo.core.domain.book;

import lombok.Getter;

public class Book {
    @Getter
    private final BookId id;


    public Book(BookId id) {
        this.id = id;
    }

    public void provideInterest(BookInterest interest) {
        interest.setId(id.uuid().toString());
    }

    public interface BookInterest {
        void setId(String id);
    }
}
