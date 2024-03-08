package com.example.demo.entrypoints;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.infrastructure.BookInMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.example.demo.entrypoints.HttpEntityFactory.httpEntityFomJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration")
@ActiveProfiles({"inMemoryRepository"})
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookInMemoryRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Nested
    class PostBook {
        @Test
        void shouldCreateBook() {
            var request = httpEntityFomJson("""
                    {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                    }
                    """);

            var response = restTemplate.postForEntity("/book/", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            ISBN isbn = new ISBN("2070541274");
            Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
            assertThat(optionalBook).isPresent();
            assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(new Book(isbn, "Harry Potter", "J. K. Rowling"));

        }
    }
}
