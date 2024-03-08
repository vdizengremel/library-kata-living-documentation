package com.example.demo.core.usecases;

import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.*;

import java.util.UUID;

public class BorrowABookUseCase {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BorrowingService borrowingService;

    public BorrowABookUseCase(MemberRepository memberRepository, BookRepository bookRepository, BorrowingService borrowingService) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.borrowingService = borrowingService;
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

        var borrowingOrError = borrowingService.borrowBook(optMember.get(), optBook.get());
        return borrowingOrError.ifSuccessOr(borrowing -> presenter.presentSuccess(), e -> presenter.presentCannotBorrowedBook());
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
