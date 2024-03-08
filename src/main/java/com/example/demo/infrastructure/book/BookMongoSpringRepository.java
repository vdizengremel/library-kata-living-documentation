package com.example.demo.infrastructure.book;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("!inMemoryRepository")
public interface BookMongoSpringRepository extends MongoRepository<BookMongoDTO, String> {
}
