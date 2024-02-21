package com.example.demo.infrastructure;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator {
    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}
