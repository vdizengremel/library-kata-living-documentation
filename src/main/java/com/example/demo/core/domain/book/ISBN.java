package com.example.demo.core.domain.book;

import com.example.annotation.CoreConcept;

import static com.example.demo.core.domain.Assertions.assertNotNullOrEmpty;

/**
 * ISBN stands for International Standard Book Number. See <a href="https://en.wikipedia.org/wiki/ISBN">wikipedia</a>.
 */
@CoreConcept
public record ISBN(String value) {
    public ISBN {
        assertNotNullOrEmpty(value, "value");
    }
}
