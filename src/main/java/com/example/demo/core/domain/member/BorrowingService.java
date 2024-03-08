package com.example.demo.core.domain.member;

import com.example.demo.core.domain.SuccessOrError;
import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.Book;

import java.time.LocalDate;

public class BorrowingService {
    private final BorrowingRepository borrowingRepository;

    private final TimeService timeService;

    public BorrowingService(BorrowingRepository borrowingRepository, TimeService timeService) {
        this.borrowingRepository = borrowingRepository;
        this.timeService = timeService;
    }

    public SuccessOrError<Borrowing, RuntimeException> borrowBook(Member member, Book book) {
        int maxNumberOfAuthorizedBorrowing = member.getMaxNumberOfAuthorizedBorrowing();

        if(borrowingRepository.countByMemberId(member.getId()) >= maxNumberOfAuthorizedBorrowing) {
            return SuccessOrError.error(new RuntimeException());
        }

        BorrowingId borrowingId = borrowingRepository.generateNewId();
        LocalDate startDate = timeService.getCurrentDate();
        Borrowing newBorrowing = member.borrow(book, startDate, borrowingId);
        borrowingRepository.add(newBorrowing);
        return SuccessOrError.success(newBorrowing);
    }
}
