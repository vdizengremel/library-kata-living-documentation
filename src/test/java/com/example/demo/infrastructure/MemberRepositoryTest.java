package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class MemberRepositoryTest {
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        memberRepository = getMemberRepository();
    }

    abstract MemberRepository getMemberRepository();

    @Test
    void shouldAddMember() {
        MemberId id = new MemberId(UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5"));
        Member member = new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", 5);
        memberRepository.add(member);

        assertThat(memberRepository.findById(id).get()).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    void shouldReturnFalseWhenMemberWithEmailDoesNotExist() {
        Member member = new Member(new MemberId(UUID.randomUUID()), "Jean", "Dupond", "jean.dupond@somemail.com", 5);
        memberRepository.add(member);

        boolean result = memberRepository.existsWithEmail("jean.durant@somemail.com");
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenMemberWithEmailExists() {
        Member member = new Member(new MemberId(UUID.randomUUID()), "Jean", "Dupond", "jean.dupond@somemail.com", 5);
        memberRepository.add(member);

        boolean result = memberRepository.existsWithEmail("jean.dupond@somemail.com");
        assertThat(result).isTrue();
    }
}
