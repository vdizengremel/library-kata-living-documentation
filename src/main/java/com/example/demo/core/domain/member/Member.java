package com.example.demo.core.domain.member;

import lombok.Getter;

public class Member {
    @Getter
    private final MemberId id;

    private String firstName;
    private String lastName;

    private String email;

    private int numberOfAuthorizedBorrowing;

    public Member(MemberId id, String firstName, String lastName, String email, int numberOfAuthorizedBorrowing) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.numberOfAuthorizedBorrowing = numberOfAuthorizedBorrowing;
    }

    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }

    public void provideInterest(MemberInterest memberInterest) {
        memberInterest.setId(id.value().toString());
        memberInterest.setFirstName(firstName);
        memberInterest.setLastName(lastName);
        memberInterest.setEmail(email);
        memberInterest.setNumberOfAuthorizedBorrowing(numberOfAuthorizedBorrowing);
    }

    public interface MemberInterest {
        void setId(String id);
        void setFirstName(String firstName);
        void setLastName(String lastName);
        void setEmail(String email);

        void setNumberOfAuthorizedBorrowing(int numberOfAuthorizedBorrowing);
    }
}
