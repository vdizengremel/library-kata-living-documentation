package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.ISBN;
import com.example.living.documentation.annotation.UseCase;
import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.member.BorrowingRepository;

@UseCase
public class ReturnABookUseCase {
    private final BorrowingRepository borrowingRepository;
    private final TimeService timeService;

    public ReturnABookUseCase(BorrowingRepository borrowingRepository, TimeService timeService) {
        this.borrowingRepository = borrowingRepository;
        this.timeService = timeService;
    }

    public <T> T execute(ReturnABookCommand command, ReturnABookPresenter<T> presenter) {
        var optionalBorrowing = borrowingRepository.findForIsbn(new ISBN(command.getIsbn()));

        if(optionalBorrowing.isEmpty()) {
            return presenter.presentBorrowingDoesNotExist();
        }

        var borrowing = optionalBorrowing.get();
        var currentDate = timeService.getCurrentDate();

        borrowing.returnBookAt(currentDate);

        borrowingRepository.update(borrowing);

        return presenter.presentBookReturned();
    }

    public interface ReturnABookCommand {
        String getIsbn();
    }

    public interface ReturnABookPresenter<T> {
        T presentBookReturned();

        T presentBorrowingDoesNotExist();
    }
}
