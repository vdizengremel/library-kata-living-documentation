package com.example.demo.core.domain.book;

import static com.example.demo.core.domain.Assertions.assertNotNullOrEmpty;

public record ISBN(String value) {
    public ISBN {
        assertNotNullOrEmpty(value, "value");
    }
}
