package com.example.demo.infrastructure.book;

import com.example.demo.ProjectMongoContainer;
import com.example.demo.core.domain.book.BookRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
@ActiveProfiles("repository-test")
public class BookAdapterTest extends AbstractBookRepository<BookRepository> {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMongoSpringRepository bookMongoSpringRepository;

    @ClassRule
//    @Container
    public static MongoDBContainer mongoDBContainer = ProjectMongoContainer.getInstance();

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDBContainer.stop();
    }

    @Override
    BookRepository getBookRepository() {
        return bookRepository;
    }

    @Override
    public void deleteAll() {
        bookMongoSpringRepository.deleteAll();
    }
}
