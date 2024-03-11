package com.example.demo.core.domain.member;

import java.util.Optional;

public interface BorrowingRepository {
    BorrowingId generateNewId();

    Optional<Borrowing> findById(BorrowingId id);
    void add(Borrowing borrowing);
    void update(Borrowing borrowing);

    long countByMemberId(MemberId memberId);
}
