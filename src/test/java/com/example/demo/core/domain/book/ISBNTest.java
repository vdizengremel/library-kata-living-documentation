package com.example.demo.core.domain.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ISBNTest {
    @Test
    void shouldCreateIsbnWithString() {
        var isbn = new ISBN("value");
        assertThat(isbn.value()).isEqualTo("value");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowWhenPassingNullOrEmpty(String value) {
        assertThatThrownBy(() -> new ISBN(value)).hasMessage("value should not be null or empty");
    }
}
