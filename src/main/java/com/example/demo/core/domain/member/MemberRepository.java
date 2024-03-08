package com.example.demo.core.domain.member;

import java.util.Optional;

public interface MemberRepository {
    MemberId generateNewId();
    void add(Member member);
    boolean existsWithEmail(String email);

    Optional<Member> findById(MemberId id);

    long countAll();

    void update(Member member);
}
