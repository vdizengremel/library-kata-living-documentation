package com.example.demo.infrastructure.borrowing;

import com.example.demo.core.domain.book.ISBN;
import com.example.living.documentation.annotation.Adapter;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.BorrowingRepository;
import com.example.demo.core.domain.member.MemberId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Adapter
public class BorrowingAdapter implements BorrowingRepository {
    @Override
    public BorrowingId generateNewId() {
        return new BorrowingId(UUID.randomUUID());
    }

    @Override
    public Optional<Borrowing> findById(BorrowingId id) {
        return Optional.empty();
    }

    @Override
    public void add(Borrowing borrowing) {

    }

    @Override
    public void update(Borrowing borrowing) {

    }

    @Override
    public List<Borrowing> findInProgressByMemberId(MemberId memberId) {
        return null;
    }

    @Override
    public Optional<Borrowing> findForIsbn(ISBN isbn) {
        return Optional.empty();
    }
}
