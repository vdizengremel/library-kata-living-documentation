package com.example.demo.infrastructure;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!inMemoryRepository")
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

    @Override
    public Optional<Member> findById(MemberId id) {
        return memberMongoSpringRepository.findById(id);
    }

    @Override
    public long countAll() {
        return memberMongoSpringRepository.count();
    }

    @Override
    public void update(Member member) {
        memberMongoSpringRepository.save(member);
    }
}
