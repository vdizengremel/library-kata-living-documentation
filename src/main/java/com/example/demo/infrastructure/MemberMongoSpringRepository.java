package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberMongoSpringRepository extends MongoRepository<Member, MemberId> {
    boolean existsByEmail(String email);
}
