package com.example.demo.core.domain.member;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberIdTest {

    @Test
    void shouldCreateMemberIdWithUuid() {
        UUID value = UUID.fromString("001b0068-1eb5-4c65-85c4-1b1eb788ecd5");
        var memberId = new MemberId(value);
        assertThat(memberId.value()).isEqualTo(value);
    }

    @Test
    void shouldThrowWhenPassingNull() {
        assertThatThrownBy(() -> new MemberId(null)).hasMessage("value should not be null");
    }
}
