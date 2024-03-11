package com.example.demo.core.usecases;

import com.example.demo.core.domain.SuccessOrError;
import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.*;

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
        var memberId = MemberId.from(command.getMemberId());
        var optMember = memberRepository.findById(memberId);

        if (optMember.isEmpty()) {
            return presenter.presentMemberDoesNotExist();
        }

        ISBN isbn = new ISBN(command.getBookIsbn());
        var optBook = bookRepository.findByIsbn(isbn);
        if (optBook.isEmpty()) {
            return presenter.presentBookDoesNotExist();
        }

        var borrowResult = borrowBook(optMember.get(), optBook.get());

        return borrowResult.ifSuccessOr(borrowing -> {
            borrowingRepository.add(borrowing);
            return presenter.presentSuccess();
        }, presenter::presentCannotBorrowedBook);
    }

    private SuccessOrError<Borrowing, BorrowingError> borrowBook(Member member, Book book) {
        var inProgressBorrowings = borrowingRepository.findInProgressByMemberId(member.getId());
        return member.borrow(book, timeService.getCurrentDate(), borrowingRepository.generateNewId(), inProgressBorrowings);
    }

    public interface BorrowABookCommand {
        String getBookIsbn();

        String getMemberId();
    }

    public interface BorrowABookPresenter<T> {
        T presentSuccess();

        T presentMemberDoesNotExist();

        T presentBookDoesNotExist();

        T presentCannotBorrowedBook(BorrowingError borrowingError);
    }
}
