package com.example.demo.infrastructure.member;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberInMemoryRepositoryTest extends AbstractMemberRepositoryTest<MemberInMemoryRepository> {
    @Override
    MemberInMemoryRepository getMemberRepository() {
        return new MemberInMemoryRepository();
    }

    @Override
    public void deleteAll() {
        memberRepository.deleteAll();
    }

    @Test
    void shouldCopyDataWhenReturningIt() {
        var memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        Member addedMember = new Member(memberId, "", "", "", MemberStatus.NEW_MEMBER);
        memberRepository.add(addedMember);

        var member = memberRepository.findById(memberId);
        assertThat(member.get()).isNotSameAs(addedMember);
    }
}
