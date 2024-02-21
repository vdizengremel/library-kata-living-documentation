package com.example.demo.core.domain;

import java.util.UUID;

public class MemberId {
    private final UUID value;

    public MemberId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }
}
