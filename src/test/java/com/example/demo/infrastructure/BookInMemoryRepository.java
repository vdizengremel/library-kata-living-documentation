package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookId;
import com.example.demo.core.domain.book.BookRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Profile("inMemoryRepository")
public class BookInMemoryRepository extends AbstractInMemoryRepository<BookId, Book> implements BookRepository {
    public static final List<BookId> BOOK_IDS = Stream.of(
                    "9a66daa6-73a0-4ad8-8c0f-288f1f06671e",
                    "baf1341b-9183-4163-aaba-bf3916fd4d27",
                    "1e3fc7e6-0610-4161-a130-6089e194ce76"
            )
            .map(UUID::fromString)
            .map(BookId::new)
            .toList();


    public BookInMemoryRepository() {
        super(BOOK_IDS);
    }

    @Override
    public void add(Book newBook) {
        super.add(newBook.getId(), newBook);
    }

    protected Book copy(Book book) {
        final BookData bookData = new BookData();
        book.provideInterest(bookData);

        return new Book(new BookId(UUID.fromString(bookData.id)));
    }

    @Setter
    private static class BookData implements Book.BookInterest {
        public String id;
    }
}
