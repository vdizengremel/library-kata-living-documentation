package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!inMemoryRepository")
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
