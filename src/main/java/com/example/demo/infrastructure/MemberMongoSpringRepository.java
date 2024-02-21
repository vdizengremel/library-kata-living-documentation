package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberMongoSpringRepository extends MongoRepository<Member, String> {
    boolean existsByEmail(String email);
}
