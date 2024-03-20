package com.example.demo.core.domain.member;

import com.example.annotation.CoreConcept;
import com.example.demo.core.domain.SuccessOrError;
import com.example.demo.core.domain.book.Book;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Person that registered to the library to borrow books.
 */
@CoreConcept
public class Member {
    @Getter
    private final MemberId id;

    @Setter
    private String firstName;

    @Setter
    private String lastName;

    private final String email;

    private MemberStatus status;

    /**
     * Register member.
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param email
     * @return new member
     */
    public static Member registerMember(MemberId id, String firstName, String lastName, String email) {
        return new Member(id, firstName, lastName, email, MemberStatus.NEW_MEMBER);
    }

    public Member(MemberId id, String firstName, String lastName, String email, MemberStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }

    /**
     * Borrow a book.
     *
     * @param book                 book to be borrowed
     * @param startDate            start date of borrowing
     * @param borrowingId          borrowing id
     * @param inProgressBorrowings in progress borrowings to check if member can borrow a new book
     * @return borrowing if succeeds or error if member cannot borrow book.
     */
    public SuccessOrError<Borrowing, BorrowingError> borrow(Book book, LocalDate startDate, BorrowingId borrowingId, List<Borrowing> inProgressBorrowings) {
        if (status == MemberStatus.BANNED) {
            return SuccessOrError.error(BorrowingError.MEMBER_IS_BANNED);
        }

        var existALateBorrowing = inProgressBorrowings.stream().anyMatch(borrowing -> borrowing.isLate(startDate));

        if (existALateBorrowing) {
            return SuccessOrError.error(BorrowingError.HAS_LATE_BORROWING);
        }

        int maxNumberOfAuthorizedBorrowing = this.getMaxNumberOfAuthorizedBorrowing();
        long countByMemberId = inProgressBorrowings.size();
        if (countByMemberId >= maxNumberOfAuthorizedBorrowing) {
            return SuccessOrError.error(BorrowingError.HAS_REACHED_MAX_AUTHORIZED_BORROWING);
        }

        return SuccessOrError.success(Borrowing.createNewBorrowing(borrowingId, getId(), book.getIsbn(), startDate));
    }

    private int getMaxNumberOfAuthorizedBorrowing() {
        return switch (status) {
            case NEW_MEMBER -> 3;
            case REGULAR -> 5;
            case RESTRICTED -> 1;
            case BANNED -> 0;
        };
    }

    public void provideInterest(MemberInterest memberInterest) {
        memberInterest.setId(id.toValueString());
        memberInterest.setFirstName(firstName);
        memberInterest.setLastName(lastName);
        memberInterest.setEmail(email);
        memberInterest.setStatus(status);
    }

    public interface MemberInterest {
        void setId(String id);

        void setFirstName(String firstName);

        void setLastName(String lastName);

        void setEmail(String email);

        void setStatus(MemberStatus status);

    }
}
