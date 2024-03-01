package com.example.demo.infrastructure;

import com.example.demo.core.domain.MemberRepository;

public class MemberInMemoryRepositoryTest extends AbstractMemberRepositoryTest {
    @Override
    MemberRepository getMemberRepository() {
        return new MemberInMemoryRepository();
    }
}
