package com.example.demo.infrastructure.member;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
import com.example.demo.core.domain.member.MemberStatus;
import com.example.demo.infrastructure.AbstractInMemoryRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Profile("inMemoryRepository")
public class MemberInMemoryRepository extends AbstractInMemoryRepository<MemberId, Member> implements MemberRepository {

    public static final List<MemberId> MEMBER_IDS = Stream.of(
                    "001b0068-1eb5-4c65-85c4-1b1eb788ecd5",
                    "111a0068-1e32-4c65-85c4-1b1eb788ecd5",
                    "222c0068-1ab8-4c65-85c4-1b1eb788ecd5",
                    "aa695a81-fd37-4f0a-a5db-292097b21cc9"
            )
            .map(UUID::fromString)
            .map(MemberId::new)
            .toList();

    public MemberInMemoryRepository() {
        super(MEMBER_IDS);
    }

    public MemberInMemoryRepository(List<MemberId> memberIds) {
        super(memberIds);
    }

    @Override
    public void add(Member member) {
        this.add(member.getId(), member);
    }

    @Override
    public boolean existsWithEmail(String email) {
        return streamData().anyMatch(member -> member.hasEmail(email));
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return streamData().filter(member -> member.getId().equals(id)).findFirst();
    }

    @Override
    public long countAll() {
        return streamData().count();
    }

    @Override
    public void update(Member member) {
        this.update(member.getId(), member);
    }

    protected Member copy(Member member) {
        final MemberData memberData = new MemberData();
        member.provideInterest(memberData);

        return new Member(MemberId.from(memberData.id), memberData.firstName, memberData.lastName, memberData.email, memberData.status);
    }

    @Setter
    public static class MemberData implements Member.MemberInterest {
        public String id;
        public String firstName;
        public String lastName;
        public String email;
        public MemberStatus status;
    }
}
