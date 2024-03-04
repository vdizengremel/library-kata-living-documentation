package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberInMemoryRepositoryTest extends AbstractMemberRepositoryTest {
    @Override
    MemberRepository getMemberRepository() {
        return new MemberInMemoryRepository();
    }

    @Test
    void shouldCopyDataWhenReturningIt() {
        MemberRepository memberRepository = getMemberRepository();
        var memberId = new MemberId(MemberInMemoryRepository.UUIDS.getFirst());
        Member addedMember = new Member(memberId, "", "", "", 5);
        memberRepository.add(addedMember);

        var member = memberRepository.findById(memberId);
        assertThat(member.get()).isNotSameAs(addedMember);
    }
}
