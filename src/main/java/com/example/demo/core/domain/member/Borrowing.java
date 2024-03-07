package com.example.demo.core.domain.member;

import com.example.demo.core.domain.book.ISBN;
import lombok.Getter;

import java.time.LocalDate;

import static com.example.demo.core.domain.Assertions.assertDateIsAfter;
import static com.example.demo.core.domain.Assertions.assertNotNull;

public final class Borrowing {
    @Getter
    private final BorrowingId id;
    private final MemberId memberId;
    private final ISBN isbn;
    private final LocalDate startDate;
    private final LocalDate maxAuthorizedReturnDate;

    public static Borrowing createNewBorrowing(BorrowingId id, MemberId memberId, ISBN isbn, LocalDate startDate) {
        return new Borrowing(id, memberId, isbn, startDate, startDate.plusWeeks(3));
    }

    public Borrowing(BorrowingId id, MemberId memberId, ISBN isbn, LocalDate startDate, LocalDate maxAuthorizedReturnDate) {
        assertNotNull(id, "borrowingId");
        assertNotNull(memberId, "memberId");
        assertNotNull(isbn, "isbn");
        assertNotNull(startDate, "startDate");
        assertNotNull(maxAuthorizedReturnDate, "maxAuthorizedReturnDate");
        assertDateIsAfter(maxAuthorizedReturnDate, startDate, "maxAuthorizedReturnDate", "startDate");

        this.id = id;
        this.memberId = memberId;
        this.isbn = isbn;
        this.startDate = startDate;
        this.maxAuthorizedReturnDate = maxAuthorizedReturnDate;
    }

    public ISBN isbn() {
        return isbn;
    }

    public void provideInterest(BorrowingInterest borrowingInterest) {
        borrowingInterest.setId(id.value().toString());
        borrowingInterest.setIsbn(isbn.value());
        borrowingInterest.setMemberId(memberId.value().toString());
        borrowingInterest.setStartDate(startDate);
        borrowingInterest.setMaxAuthorizedReturnDate(maxAuthorizedReturnDate);
    }

    public boolean by(MemberId memberId) {
        return this.memberId.equals(memberId);
    }

    public interface BorrowingInterest {
        void setId(String id);
        void setIsbn(String isbn);
        void setMemberId(String memberId);
        void setStartDate(LocalDate startDate);
        void setMaxAuthorizedReturnDate(LocalDate maxAuthorizedReturnDate);
    }
}
