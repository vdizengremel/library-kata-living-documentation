package com.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocGeneratorTest {

    @Test
    void shouldSanitiseComment(){
        var result = DocGenerator.sanitiseComment("Borrowing of a {@link com.example.demo.core.domain.book.Book book} by a {@link Member member}.");
        assertThat(result).isEqualTo("Borrowing of a book by a member.");
    }
}
