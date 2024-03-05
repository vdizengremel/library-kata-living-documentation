package com.example.demo.infrastructure;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberRepository;
import com.example.demo.core.domain.member.MemberStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberInMemoryRepositoryTest extends AbstractMemberRepositoryTest {
    @Override
    MemberRepository getMemberRepository() {
        return new MemberInMemoryRepository();
    }

    @Test
    void shouldCopyDataWhenReturningIt() {
        MemberRepository memberRepository = getMemberRepository();
        var memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        Member addedMember = new Member(memberId, "", "", "", MemberStatus.NEW_MEMBER);
        memberRepository.add(addedMember);

        var member = memberRepository.findById(memberId);
        assertThat(member.get()).isNotSameAs(addedMember);
    }
}
