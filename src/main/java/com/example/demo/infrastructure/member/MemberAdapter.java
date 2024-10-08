package com.example.demo.infrastructure.member;

import com.example.living.documentation.annotation.Adapter;
import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;

import java.util.Optional;
import java.util.UUID;

@Adapter
public class MemberAdapter implements MemberRepository {
    private final MemberMongoSpringRepository memberMongoSpringRepository;

    public MemberAdapter(MemberMongoSpringRepository memberMongoSpringRepository) {
        this.memberMongoSpringRepository = memberMongoSpringRepository;
    }

    @Override
    public MemberId generateNewId() {
        return new MemberId(UUID.randomUUID());
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
