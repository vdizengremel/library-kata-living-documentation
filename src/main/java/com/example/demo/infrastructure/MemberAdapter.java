package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberAdapter implements MemberRepository {
    private final MemberMongoSpringRepository memberMongoSpringRepository;
    private final UUIDGenerator uuidGenerator;

    public MemberAdapter(MemberMongoSpringRepository memberMongoSpringRepository, UUIDGenerator uuidGenerator) {
        this.memberMongoSpringRepository = memberMongoSpringRepository;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public MemberId generateNewId() {
        return new MemberId(uuidGenerator.generateUUID());
    }

    @Override
    public void add(Member member) {
        memberMongoSpringRepository.insert(member);
    }

    @Override
    public boolean existsWithEmail(String email) {
        return memberMongoSpringRepository.existsByEmail(email);
    }
}
