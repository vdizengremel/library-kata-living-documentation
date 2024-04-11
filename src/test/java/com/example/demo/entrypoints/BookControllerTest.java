package com.example.demo.entrypoints;

import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.example.demo.entrypoints.HttpEntityFactory.httpEntityFomJson;
import static org.assertj.core.api.Assertions.assertThat;

@ControllerTest
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
        void shouldCreateBookAndReturnOkWhenBookDoesNotExist() {
            var request = httpEntityFomJson("""
                    {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                    }
                    """);

            var response = restTemplate.postForEntity("/book/", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            ISBN isbn = new ISBN("2070541274");
            Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
            assertThat(optionalBook).isPresent();
            assertThat(optionalBook.get()).usingRecursiveComparison().isEqualTo(new Book(isbn, "Harry Potter", "J. K. Rowling"));

        }

        @Test
        void shouldReturnBadRequestWhenBookAlreadyExists() {
            var request = httpEntityFomJson("""
                    {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                    }
                    """);

            restTemplate.postForEntity("/book/", request, String.class);
            var response = restTemplate.postForEntity("/book/", request, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    class GetBookById {
        @Test
        void shouldReturn200WithBookWhenItExists() throws JSONException {
            ISBN isbn = new ISBN("2070541274");
            bookRepository.register(new Book(isbn, "Harry Potter", "J. K. Rowling"));

            var response = restTemplate.getForEntity("/book/2070541274", String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            var expectedBody = """
                     {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                     }
                    """;
            JSONAssert.assertEquals(expectedBody, response.getBody(), JSONCompareMode.STRICT);
        }

        @Test
        void shouldReturn404WhenBookDoesNotExist() {
            var response = restTemplate.getForEntity("/book/2070541274", String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
