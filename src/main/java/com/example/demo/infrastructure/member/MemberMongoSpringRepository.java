package com.example.demo.infrastructure.member;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("!inMemoryRepository")
public interface MemberMongoSpringRepository extends MongoRepository<Member, MemberId> {
    boolean existsByEmail(String email);
}
