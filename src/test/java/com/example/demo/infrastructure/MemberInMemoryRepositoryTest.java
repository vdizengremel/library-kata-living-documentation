package com.example.demo.infrastructure;

import com.example.demo.core.domain.MemberRepository;
import org.mockito.Mockito;

public class MemberInMemoryRepositoryTest extends MemberRepositoryTest{
    @Override
    MemberRepository getMemberRepository() {
        return new MemberInMemoryRepository(Mockito.mock());
    }
}
