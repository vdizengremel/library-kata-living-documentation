package com.example.demo.infrastructure.book;

import com.example.annotation.Adapter;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;

import java.util.Optional;

@Adapter
public class BookAdapter implements BookRepository {
    private final BookMongoSpringRepository bookMongoSpringRepository;

    public BookAdapter(BookMongoSpringRepository bookMongoSpringRepository) {
        this.bookMongoSpringRepository = bookMongoSpringRepository;
    }

    @Override
    public Optional<Book> findByIsbn(ISBN isbn) {
        Optional<BookMongoDTO> optionalBookMongoDTO = bookMongoSpringRepository.findById(isbn.value());
        return optionalBookMongoDTO.map(bookMongoDTO -> new Book(new ISBN(bookMongoDTO.id), bookMongoDTO.title, bookMongoDTO.author));
    }

    @Override
    public void register(Book newBook) {
        BookMongoDTO bookMongoDTO = new BookMongoDTO();
        newBook.provideInterest(bookMongoDTO);
        bookMongoSpringRepository.insert(bookMongoDTO);
    }
}
