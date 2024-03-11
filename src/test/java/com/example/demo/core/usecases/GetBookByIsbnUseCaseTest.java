package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetBookByIsbnUseCaseTest {
    private GetBookByIsbnUseCase getBookByIsbnUseCase;
    private BookInMemoryRepository bookInMemoryRepository;

    @BeforeEach
    void setUp() {
        bookInMemoryRepository = new BookInMemoryRepository();
        getBookByIsbnUseCase = new GetBookByIsbnUseCase(bookInMemoryRepository);
    }

    @Test
    void shouldReturnBookWhenBookExists(){
        bookInMemoryRepository.register(new Book(new ISBN("123"), "Harry Potter", "Rowling"));
        Book starWars = new Book(new ISBN("456"), "Star Wars", "Lucas");
        bookInMemoryRepository.register(starWars);

        Book foundBook = getBookByIsbnUseCase.execute(() -> "456", new PresenterForTest());
        assertThat(foundBook).usingRecursiveComparison().isEqualTo(starWars);
    }

    @Test
    void shouldThrowWhenBookDoesNotExist(){
        bookInMemoryRepository.register(new Book(new ISBN("123"), "Harry Potter", "Rowling"));

        assertThatThrownBy(() -> getBookByIsbnUseCase.execute(() -> "456", new PresenterForTest())).hasMessage("book not found");
    }

    static class PresenterForTest implements GetBookByIsbnUseCase.GetBookByIsbnPresenter<Book> {

        @Override
        public Book presentBook(Book book) {
            return book;
        }

        @Override
        public Book presentBookNotFound() {
            throw new RuntimeException("book not found");
        }
    }
}
