package com.example.demo.core.usecases;

import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.*;
import io.vavr.control.Either;

import java.time.LocalDate;
import java.util.UUID;

public class BorrowABookUseCase {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final TimeService timeService;

    public BorrowABookUseCase(MemberRepository memberRepository, BookRepository bookRepository, BorrowingRepository borrowingRepository, TimeService timeService) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.timeService = timeService;
    }

    public <T> T execute(BorrowABookCommand command, BorrowABookPresenter<T> presenter) {
        var memberId = new MemberId(UUID.fromString(command.getMemberId()));
        var optMember = memberRepository.findById(memberId);

        if(optMember.isEmpty()) {
            return presenter.presentMemberDoesNotExist();
        }

        ISBN isbn = new ISBN(command.getBookIsbn());
        var optBook = bookRepository.findByIsbn(isbn);
        if(optBook.isEmpty()) {
            return presenter.presentBookDoesNotExist();
        }

        var either = borrowBook(optMember.get(), optBook.get());
        return either.fold(e -> presenter.presentCannotBorrowedBook(), borrowing -> presenter.presentSuccess());
    }

    private Either<RuntimeException, Borrowing> borrowBook(Member member, Book book) {
        int maxNumberOfAuthorizedBorrowing = member.getMaxNumberOfAuthorizedBorrowing();

        if(borrowingRepository.countByMemberId(member.getId()) >= maxNumberOfAuthorizedBorrowing) {
            return Either.left(new RuntimeException());
        }

        BorrowingId borrowingId = borrowingRepository.generateNewId();
        LocalDate startDate = timeService.getCurrentDate();
        Borrowing newBorrowing = member.borrow(book.getIsbn(), startDate, borrowingId);
        borrowingRepository.add(newBorrowing);
        return Either.right(newBorrowing);
    }

    public interface BorrowABookCommand {
        String getBookIsbn();
        String getMemberId();
    }

    public interface BorrowABookPresenter<T> {
        T presentSuccess();
        T presentMemberDoesNotExist();
        T presentBookDoesNotExist();

        T presentCannotBorrowedBook();
    }
}
