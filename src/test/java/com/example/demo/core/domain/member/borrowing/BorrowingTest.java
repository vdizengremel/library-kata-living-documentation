package com.example.demo.core.domain.member.borrowing;

import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.MemberId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BorrowingTest {
    private BorrowingId validBorrowingId;
    private MemberId validMemberId;
    private ISBN validIsbn;
    private final LocalDate validStartDate = LocalDate.MIN;
    private final LocalDate validMaxAuthorizedReturnDate = LocalDate.MAX;

    @BeforeEach
    void setUp() {
        validBorrowingId = new BorrowingId(UUID.randomUUID());
        validMemberId = new MemberId(UUID.randomUUID());
        validIsbn = new ISBN("isbn");
    }

    @Test
    void shouldCreateBorrowingWhenAllInformationAreValid() {
        assertThatCode(() -> new Borrowing(validBorrowingId, validMemberId, validIsbn, validStartDate, validMaxAuthorizedReturnDate)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowWhenPassingNullBorrowingId() {
        assertThatThrownBy(() -> new Borrowing(null, validMemberId, validIsbn, validStartDate, validMaxAuthorizedReturnDate)).hasMessage("borrowingId should not be null");
    }

    @Test
    void shouldThrowWhenPassingNullMemberId() {
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, null, validIsbn, validStartDate, validMaxAuthorizedReturnDate)).hasMessage("memberId should not be null");
    }

    @Test
    void shouldThrowWhenPassingNullIsbn() {
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, validMemberId, null, validStartDate, validMaxAuthorizedReturnDate)).hasMessage("isbn should not be null");
    }

    @Test
    void shouldThrowWhenPassingNullStartDate() {
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, validMemberId, validIsbn, null, validMaxAuthorizedReturnDate)).hasMessage("startDate should not be null");
    }

    @Test
    void shouldThrowWhenPassingNullMaxAuthorizedReturnDate() {
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, validMemberId, validIsbn, validStartDate, null)).hasMessage("maxAuthorizedReturnDate should not be null");
    }

    @Test
    void shouldThrowWhenPassingMaxAuthorizedReturnDateBeforeStartDate() {
        var startDate = LocalDate.of(2024, 3, 6);
        var maxAuthorizedReturnDate = startDate.minusDays(1);
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, validMemberId, validIsbn, startDate, maxAuthorizedReturnDate)).hasMessage("maxAuthorizedReturnDate should be after startDate");
    }

    @Test
    void shouldThrowWhenPassingMaxAuthorizedReturnDateEqualToStartDate() {
        var startDate = LocalDate.of(2024, 3, 6);
        assertThatThrownBy(() -> new Borrowing(validBorrowingId, validMemberId, validIsbn, startDate, startDate)).hasMessage("maxAuthorizedReturnDate should be after startDate");
    }

}
