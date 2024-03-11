package com.example.demo.core.usecases;

import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.BorrowingRepository;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ReturnABookUseCaseTest {
    private ReturnABookUseCase returnABookUseCase;
    private BorrowingRepository borrowingRepository;
    private TimeService timeService;

    @BeforeEach
    void setUp() {
        borrowingRepository = new BorrowingInMemoryRepository();
        timeService = Mockito.mock();

        returnABookUseCase = new ReturnABookUseCase(borrowingRepository, timeService);
    }

    @Test
    void shouldPresentSuccessWhenBorrowingExists() {
        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();

        BorrowingId borrowingId = borrowingRepository.generateNewId();
        borrowingRepository.add(new Borrowing(borrowingId, memberId, new ISBN("123"), LocalDate.of(2024, 3, 11), LocalDate.MAX, null));

        String result = returnABookUseCase.execute(borrowingId::toStringValue, new PresenterForTest());
        assertThat(result).isEqualTo("success");
    }

    @Test
    void shouldSaveBorrowingWithReturnDateWhenBorrowingExists() {
        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();

        BorrowingId borrowingId = borrowingRepository.generateNewId();
        Borrowing initialBorrowing = new Borrowing(borrowingId, memberId, new ISBN("123"), LocalDate.of(2024, 3, 6), LocalDate.MAX, null);
        borrowingRepository.add(initialBorrowing);

        LocalDate currentDate = LocalDate.of(2024, 3, 11);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        returnABookUseCase.execute(borrowingId::toStringValue, new PresenterForTest());

        var updatedBorrowing = borrowingRepository.findById(borrowingId);
        assertThat(updatedBorrowing.get()).usingRecursiveComparison().isEqualTo(new Borrowing(borrowingId, memberId, new ISBN("123"), LocalDate.of(2024, 3, 6), LocalDate.MAX, currentDate));
    }

    static class PresenterForTest implements ReturnABookUseCase.ReturnABookPresenter<String> {

        @Override
        public String presentBookReturned() {
            return "success";
        }

        @Override
        public String presentBorriwingDoesNotExist() {
            throw new RuntimeException("borrowing does not exist");
        }
    }
}
