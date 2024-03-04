package com.example.demo.infrastructure;

import com.example.demo.core.domain.member.Member;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.MemberRepository;
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
                    "222c0068-1ab8-4c65-85c4-1b1eb788ecd5"
            )
            .map(UUID::fromString)
            .map(MemberId::new)
            .toList();

    private int uuidIndex;

    public MemberInMemoryRepository() {
        super(MEMBER_IDS);
    }


    @Override
    public MemberId generateNewId() {
        MemberId memberId = MEMBER_IDS.get(uuidIndex);
        uuidIndex++;
        return memberId;
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

    protected Member copy(Member member) {
        final MemberData memberData = new MemberData();
        member.provideInterest(memberData);

        return new Member(new MemberId(UUID.fromString(memberData.id)), memberData.firstName, memberData.lastName, memberData.email, memberData.numberOfAuthorizedBorrowing);
    }

    @Setter
    public static class MemberData implements Member.MemberInterest {
        public String id;
        public String firstName;
        public String lastName;
        public String email;
        public int numberOfAuthorizedBorrowing;
    }
}
