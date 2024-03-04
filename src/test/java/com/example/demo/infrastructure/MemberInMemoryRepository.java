package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

@Component
@Profile("inMemoryRepository")
public class MemberInMemoryRepository implements MemberRepository {
    private final Map<MemberId, Member> membersById;

    public static final List<UUID> UUIDS = List.of(
            UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5"),
            UUID.fromString("111a0068-1e32-4c65-85c4-1b1eb788ecd5"),
            UUID.fromString("222c0068-1ab8-4c65-85c4-1b1eb788ecd5")
    );

    private int uuidIndex;

    public MemberInMemoryRepository() {
        this.membersById = new HashMap<>();
        this.uuidIndex = 0;
    }


    @Override
    public MemberId generateNewId() {
        MemberId memberId = new MemberId(UUIDS.get(uuidIndex));
        uuidIndex++;
        return memberId;
    }

    @Override
    public void add(Member member) {
        membersById.put(member.getId(), member);
    }

    @Override
    public boolean existsWithEmail(String email) {
        return getMemberStream().anyMatch(member -> member.hasEmail(email));
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return getMemberStream().filter(member -> member.getId().equals(id)).findFirst();
    }

    @Override
    public long countAll() {
        return membersById.values().size();
    }

    private Stream<Member> getMemberStream() {
        return membersById.values().stream().map(this::copy);
    }

    private Member copy(Member member) {
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
