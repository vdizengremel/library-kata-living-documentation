package com.example.demo.core.domain.member;

import java.util.UUID;

import static com.example.demo.core.domain.Assertions.assertNotNull;

public record BorrowingId(UUID value) {
    public BorrowingId {
        assertNotNull(value, "value");
    }

    public static BorrowingId fromString(String value) {
        return new BorrowingId(UUID.fromString(value));
    }

    public String toStringValue() {
        return value.toString();
    }
}
