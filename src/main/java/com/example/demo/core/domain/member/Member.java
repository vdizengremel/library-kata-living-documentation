package com.example.demo.core.domain.member;

import com.example.demo.core.domain.book.ISBN;
import lombok.Getter;

import java.time.LocalDate;

public class Member {
    @Getter
    private final MemberId id;

    private String firstName;
    private String lastName;

    private String email;

    private MemberStatus status;

    public static Member createNewMember(MemberId id, String firstName, String lastName, String email) {
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

    public Borrowing borrow(ISBN isbn, LocalDate startDate, BorrowingId borrowingId) {
        return Borrowing.createNewBorrowing(borrowingId, getId(), isbn, startDate);
    }

    public int getMaxNumberOfAuthorizedBorrowing() {
        return switch (status) {
            case NEW_MEMBER -> 3;
            case REGULAR -> 5;
            case RESTRICTED -> 1;
            case BANNED -> 0;
        };
    }

    public void provideInterest(MemberInterest memberInterest) {
        memberInterest.setId(id.value().toString());
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
