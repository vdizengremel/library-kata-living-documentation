package com.example.demo.core.usecases;

import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.BorrowingRepository;
import org.springframework.stereotype.Service;

@Service
public class ReturnABookUseCase {
    private final BorrowingRepository borrowingRepository;
    private final TimeService timeService;

    public ReturnABookUseCase(BorrowingRepository borrowingRepository, TimeService timeService) {
        this.borrowingRepository = borrowingRepository;
        this.timeService = timeService;
    }

    public <T> T execute(ReturnABookCommand command, ReturnABookPresenter<T> presenter) {
        var optionalBorrowing = borrowingRepository.findById(BorrowingId.fromString(command.borrowingId()));

        if(optionalBorrowing.isEmpty()) {
            return presenter.presentBorrowingDoesNotExist();
        }

        var borrowing = optionalBorrowing.get();
        var currentDate = timeService.getCurrentDate();

        borrowing.saveBookReturnedAt(currentDate);

        borrowingRepository.update(borrowing);

        return presenter.presentBookReturned();
    }

    public interface ReturnABookCommand {
        String borrowingId();
    }

    public interface ReturnABookPresenter<T> {
        T presentBookReturned();

        T presentBorrowingDoesNotExist();
    }
}
