package com.example.demo.entrypoints;

import com.example.demo.core.usecases.RegisterABookUseCase;
import lombok.Getter;

@Getter
public class BookDTO implements RegisterABookUseCase.RegisterABookCommand {
    public String isbn;
    public String title;
    public String author;
}
