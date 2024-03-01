package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("!inMemoryRepository")
public interface MemberMongoSpringRepository extends MongoRepository<Member, MemberId> {
    boolean existsByEmail(String email);
}
