package com.example.demo.core.domain.member;

import com.example.demo.core.domain.book.ISBN;

import java.util.List;
import java.util.Optional;

public interface BorrowingRepository {
    BorrowingId generateNewId();

    Optional<Borrowing> findById(BorrowingId id);
    void add(Borrowing borrowing);
    void update(Borrowing borrowing);

    List<Borrowing> findInProgressByMemberId(MemberId memberId);
    Optional<Borrowing> findForIsbn(ISBN isbn);
}
