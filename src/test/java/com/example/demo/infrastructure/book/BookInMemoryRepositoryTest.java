package com.example.demo.infrastructure.book;

public class BookInMemoryRepositoryTest extends AbstractBookRepository<BookInMemoryRepository> {
    @Override
    BookInMemoryRepository getBookRepository() {
        return new BookInMemoryRepository();
    }

    @Override
    public void deleteAll() {
        bookRepository.deleteAll();
    }
}
