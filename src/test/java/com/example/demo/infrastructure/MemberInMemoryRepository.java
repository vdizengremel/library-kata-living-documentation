package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;

import java.util.*;

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
        return membersById.values().stream().anyMatch(member -> member.hasEmail(email));
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return membersById.values().stream().filter(member -> member.getId().equals(id)).findFirst();
    }

    @Override
    public long countAll() {
        return membersById.values().size();
    }
}
