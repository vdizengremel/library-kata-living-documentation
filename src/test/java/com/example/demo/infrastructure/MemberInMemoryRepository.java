package com.example.demo.infrastructure;

import com.example.demo.core.domain.Member;
import com.example.demo.core.domain.MemberId;
import com.example.demo.core.domain.MemberRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemberInMemoryRepository implements MemberRepository {
    private final UUIDGenerator uuidGenerator;
    private final Map<MemberId, Member> membersById;

    public MemberInMemoryRepository(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
        this.membersById = new HashMap<>();
    }


    @Override
    public MemberId generateNewId() {
        return new MemberId(uuidGenerator.generateUUID());
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
