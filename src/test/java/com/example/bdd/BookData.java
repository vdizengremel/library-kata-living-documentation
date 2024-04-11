package com.example.bdd;

import com.example.bdd.feature.stepdefinitions.BookStepDefinitions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class BookData {
    private static final Config books
            = ConfigFactory.load("testdata/books");

    public BookStepDefinitions.RegisterABookCommand getRegisterABookCommandByBookName(String bookName) {
        Config config = books.getConfig(bookName);
        return BookStepDefinitions.RegisterABookCommand.builder()
                .isbn(config.getString("isbn"))
                .title(config.getString("title"))
                .author(config.getString("author"))
                .build();
    }

    public String getISBNFor(String name) {
        return getRegisterABookCommandByBookName(name).getIsbn();
    }
}
