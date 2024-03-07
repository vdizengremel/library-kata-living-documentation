package com.example.demo.core.domain.member.borrowing;

import com.example.demo.core.domain.member.BorrowingId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BorrowingIdTest {
    @Test
    void shouldCreateBorrowingIdWithUuid() {
        UUID value = UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5");
        var memberId = new BorrowingId(value);
        assertThat(memberId.value()).isEqualTo(value);
    }

    @Test
    void shouldThrowWhenPassingNull() {
        assertThatThrownBy(() -> new BorrowingId(null)).hasMessage("value should not be null");
    }

}
