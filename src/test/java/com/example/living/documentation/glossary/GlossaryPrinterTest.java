package com.example.living.documentation.glossary;

import com.example.living.documentation.glossary.Glossary;
import com.example.living.documentation.glossary.GlossaryItem;
import com.example.living.documentation.glossary.GlossaryPrinter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static com.example.living.documentation.glossary.GlossaryPrinter.NEW_LINE;
import static org.assertj.core.api.Assertions.assertThat;

class GlossaryPrinterTest {

    @Test
    void shouldPrintGlossaryWithACompleteConcept() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem bookConcept = new GlossaryItem("Book", "Just a book", Map.of("isbn", "unique id", "title", "given title"), Map.of("borrow", "borrow the book", "return", "return the book"));

        Glossary glossary = new Glossary();
        glossary.addItem(bookConcept);

        glossaryPrinter.printGlossary(glossary);

        String expectedStringBuilder = "Book:: Just a book" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Information:" +
                                       NEW_LINE +
                                       "- isbn: unique id" +
                                       NEW_LINE +
                                       "- title: given title" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Operations:" +
                                       NEW_LINE +
                                       "- borrow: borrow the book" +
                                       NEW_LINE +
                                       "- return: return the book" +
                                       NEW_LINE +
                                       NEW_LINE;

        assertThat(stringBuilder.toString()).isEqualTo(expectedStringBuilder);
    }

    @Test
    void shouldNotContainInformationWhenNone() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem bookConcept = new GlossaryItem("Book", "Just a book", Collections.emptyMap(), Map.of("borrow", "borrow the book", "return", "return the book"));

        Glossary glossary = new Glossary();
        glossary.addItem(bookConcept);

        glossaryPrinter.printGlossary(glossary);

        String expectedStringBuilder = "Book:: Just a book" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Operations:" +
                                       NEW_LINE +
                                       "- borrow: borrow the book" +
                                       NEW_LINE +
                                       "- return: return the book" +
                                       NEW_LINE +
                                       NEW_LINE;

        assertThat(stringBuilder.toString()).isEqualTo(expectedStringBuilder);
    }

    @Test
    void shouldNotContainOperationsWhenNone() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem bookConcept = new GlossaryItem("Book", "Just a book", Map.of("isbn", "unique id", "title", "given title"), Collections.emptyMap());

        Glossary glossary = new Glossary();
        glossary.addItem(bookConcept);

        glossaryPrinter.printGlossary(glossary);

        String expectedStringBuilder = "Book:: Just a book" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Information:" +
                                       NEW_LINE +
                                       "- isbn: unique id" +
                                       NEW_LINE +
                                       "- title: given title" +
                                       NEW_LINE +
                                       NEW_LINE;

        assertThat(stringBuilder.toString()).isEqualTo(expectedStringBuilder);
    }

    @Test
    void shouldPrintInformationAndOperationCorrectlyWhenNoDefinition() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem bookConcept = new GlossaryItem("Book", "Just a book", Map.of("isbn", ""), Map.of("borrow", ""));

        Glossary glossary = new Glossary();
        glossary.addItem(bookConcept);

        glossaryPrinter.printGlossary(glossary);

        String expectedStringBuilder = "Book:: Just a book" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Information:" +
                                       NEW_LINE +
                                       "- isbn" +
                                       NEW_LINE +
                                       NEW_LINE +
                                       "Operations:" +
                                       NEW_LINE +
                                       "- borrow" +
                                       NEW_LINE +
                                       NEW_LINE;

        assertThat(stringBuilder.toString()).isEqualTo(expectedStringBuilder);
    }

    @Test
    void shouldPrintItemsInAlphabeticalOrder() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem bookConcept = new GlossaryItem("Book", "Just a book", Map.of("isbn", ""), Map.of("borrow", ""));
        GlossaryItem memberConcept = new GlossaryItem("Member", "Just a member", Map.of("id", ""), Map.of("update", ""));

        Glossary glossary = new Glossary();
        glossary.addItem(memberConcept);
        glossary.addItem(bookConcept);

        glossaryPrinter.printGlossary(glossary);

        String actual = stringBuilder.toString();
        assertThat(actual.indexOf("Book::")).isLessThan(actual.indexOf("Member::"));
    }

    @Test
    void shouldPrintHtmlLinkAsAsciiDocLink() {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);

        GlossaryItem itemWithLink = new GlossaryItem("ImportantConceptWithLink", "Concept with a <a href=\\\"https://wiki.com\\\">link</a>.", Collections.emptyMap(), Collections.emptyMap());

        Glossary glossary = new Glossary();
        glossary.addItem(itemWithLink);

        glossaryPrinter.printGlossary(glossary);

        String actual = stringBuilder.toString();
        assertThat(actual).isEqualTo("ImportantConceptWithLink:: Concept with a https://wiki.com[link,window=_blank]." + NEW_LINE + NEW_LINE);
    }
}
