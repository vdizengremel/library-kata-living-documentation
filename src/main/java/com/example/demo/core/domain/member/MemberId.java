package com.example.demo.core.domain.member;

import java.util.UUID;

import static com.example.demo.core.domain.Assertions.assertNotNull;

public record MemberId(UUID value) {
    public MemberId {
        assertNotNull(value, "value");
    }
}
