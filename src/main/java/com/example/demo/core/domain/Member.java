package com.example.demo.core.domain;

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
        memberInterest.informId(id.value().toString());
        memberInterest.informFirstName(firstName);
        memberInterest.informLastName(lastName);
        memberInterest.informEmail(email);
    }

    public interface MemberInterest {
        void informId(String id);
        void informFirstName(String firstName);
        void informLastName(String lastName);
        void informEmail(String email);
    }
}
