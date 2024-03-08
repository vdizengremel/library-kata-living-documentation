package com.example.demo.infrastructure;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
import com.example.demo.core.domain.member.MemberStatus;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractMemberRepositoryTest<T extends MemberRepository> {
    protected T memberRepository;

    @BeforeEach
    void setUp(){
        memberRepository = getMemberRepository();
    }

    abstract T getMemberRepository();

    @AfterEach
    public abstract void deleteAll();

    @Test
    void shouldGenerateIds() {
        var firstId = memberRepository.generateNewId();
        var secondId = memberRepository.generateNewId();

        assertThat(firstId.value()).isNotEqualTo(secondId.value());
    }

    @Nested
    class Add {
        @Test
        void shouldAddMember() {
            MemberId id = MemberId.from("001b0068-1eb5-4c65-85c4-1b1eb788ecd5");
            Member member = new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
            memberRepository.add(member);

            assertThat(memberRepository.findById(id).get()).usingRecursiveComparison().isEqualTo(member);
        }

        @Test
        void shouldThrowWhenAddingMemberWithSameId() {
            MemberId id = MemberId.from("001b0068-1eb5-4c65-85c4-1b1eb788ecd5");
            Member member = new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
            memberRepository.add(member);

            assertThatThrownBy(() -> memberRepository.add(member)).isInstanceOf(Exception.class);
        }
    }

    @Nested
    class ExistsWithEmail {
        @Test
        void shouldReturnFalseWhenMemberWithEmailDoesNotExist() {
            Member member = new Member(new MemberId(UUID.randomUUID()), "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
            memberRepository.add(member);

            boolean result = memberRepository.existsWithEmail("jean.durant@somemail.com");
            assertThat(result).isFalse();
        }

        @Test
        void shouldReturnTrueWhenMemberWithEmailExists() {
            Member member = new Member(new MemberId(UUID.randomUUID()), "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
            memberRepository.add(member);

            boolean result = memberRepository.existsWithEmail("jean.dupond@somemail.com");
            assertThat(result).isTrue();
        }
    }

    @Test
    void shouldUpdateMember() {
        MemberId id = MemberId.from("001b0068-1eb5-4c65-85c4-1b1eb788ecd5");
        Member member = new Member(id, "Jean", "Dupond", "jean.dupond@somemail.com", MemberStatus.NEW_MEMBER);
        memberRepository.add(member);

        Member updatedMember = new Member(id, "Paul", "Durant", "paul.durant@somemail.com", MemberStatus.NEW_MEMBER);
        memberRepository.update(updatedMember);

        assertThat(memberRepository.findById(id).get()).usingRecursiveComparison().isEqualTo(updatedMember);
    }
}
