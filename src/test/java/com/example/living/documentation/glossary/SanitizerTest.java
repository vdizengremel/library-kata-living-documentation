package com.example.living.documentation.glossary;

import com.example.living.documentation.glossary.Sanitizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SanitizerTest {
    @Test
    void shouldSanitiseCommentWhenContainsJavadocLink(){
        var result = Sanitizer.sanitizeComment("Borrowing of a {@link com.example.demo.core.domain.book.Book book} by a {@link Member member}.");
        assertThat(result).isEqualTo("Borrowing of a book by a member.");
    }

    @Test
    void shouldSanitiseCommentWhenContainsHtmlLink(){
        var result = Sanitizer.sanitizeComment("Concept with a <a href=\\\"https://wiki.com\\\">link</a> and <a href=\\\"https://another.com\\\">another</a>.");
        assertThat(result).isEqualTo("Concept with a https://wiki.com[link,window=_blank] and https://another.com[another,window=_blank].");
    }
}
