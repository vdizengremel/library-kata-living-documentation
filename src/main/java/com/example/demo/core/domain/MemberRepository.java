package com.example.demo.core.domain;

public interface MemberRepository {
    MemberId generateNewId();
    void add(Member member);
    boolean existsWithEmail(String email);

}
