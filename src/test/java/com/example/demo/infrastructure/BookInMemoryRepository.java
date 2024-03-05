package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.book.BookRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("inMemoryRepository")
public class BookInMemoryRepository extends AbstractInMemoryRepository<ISBN, Book> implements BookRepository {

    public BookInMemoryRepository() {
        super(Collections.emptyList());
    }

    @Override
    public Optional<Book> findByIsbn(ISBN isbn) {
        return this.findById(isbn);
    }

    @Override
    public void register(Book newBook) {
        super.add(newBook.getIsbn(), newBook);
    }

    protected Book copy(Book book) {
        final BookData bookData = new BookData();
        book.provideInterest(bookData);

        return new Book(new ISBN(bookData.id), bookData.title, bookData.author);
    }

    @Setter
    private static class BookData implements Book.BookInterest {
        public String id;
        public String title;
        public String author;
    }
}
