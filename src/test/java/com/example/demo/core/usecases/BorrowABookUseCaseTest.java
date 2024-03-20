package com.example.demo.core.usecases;

import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.Book;
import com.example.demo.core.domain.book.BookRepository;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.*;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


class BorrowABookUseCaseTest {
    private BorrowABookUseCase borrowABookUseCase;
    private MemberRepository memberRepository;
    private BookRepository bookRepository;
    private BorrowingRepository borrowingRepository;
    private TimeService timeService;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberInMemoryRepository();
        bookRepository = new BookInMemoryRepository();
        borrowingRepository = new BorrowingInMemoryRepository();
        timeService = Mockito.mock(TimeService.class);

        borrowABookUseCase = new BorrowABookUseCase(memberRepository, bookRepository, borrowingRepository, timeService);
    }

    @Test
    void shouldPresentErrorWhenMemberDoesNotExists() {
        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThatThrownBy(() -> borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).hasMessage("Member does not exist");
    }

    @Test
    void shouldPresentErrorWhenBookDoesNotExists() {
        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        memberRepository.add(Member.registerMember(memberId, "Jean", "Dupond", "jean.dupond@smth.com"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThatThrownBy(() -> borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).hasMessage("Book does not exist");
    }

    @Test
    void shouldBorrowBookWhenMemberAndBookExist() {
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        memberRepository.add(Member.registerMember(memberId, "Jean", "Dupond", "jean.dupond@smth.com"));

        ISBN isbn = new ISBN("isbn");
        bookRepository.register(new Book(isbn, "title", "author"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        var result = borrowABookUseCase.execute(command, new BorrowABookPresenterForTest());
        assertThat(result).isEqualTo("success");

        var optBorrowing = borrowingRepository.findById(BorrowingInMemoryRepository.IDS.getFirst());
        assertThat(optBorrowing).isPresent();
        assertThat(optBorrowing.get()).usingRecursiveComparison().isEqualTo(new Borrowing(BorrowingInMemoryRepository.IDS.getFirst(), memberId, isbn, currentDate, LocalDate.of(2024, 3, 27), null));
    }

    @Test
    void shouldNotBorrowBookWhenMemberHasAlreadyBorrowedThreeBooks() {
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        memberRepository.add(Member.registerMember(memberId, "Jean", "Dupond", "jean.dupond@smth.com"));

        List<String> isbns = List.of(
                "Book 1",
                "Book 2",
                "Book 3"
        );

        registerBooks(isbns);
        borrowBooks(isbns, memberId);


        ISBN isbn = new ISBN("isbn");
        bookRepository.register(new Book(isbn, "title", "author"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThatThrownBy(() -> borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).hasMessage("Cannot borrow book: " + BorrowingError.HAS_REACHED_MAX_AUTHORIZED_BORROWING);
        assertThat(borrowingRepository.findInProgressByMemberId(memberId)).hasSize(3);
    }

    @Test
    void shouldNotBorrowBookWhenMemberHasALateBorrowing() {
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        MemberId memberId = MemberInMemoryRepository.MEMBER_IDS.getFirst();
        memberRepository.add(Member.registerMember(memberId, "Jean", "Dupond", "jean.dupond@smth.com"));


        BorrowingId borrowingId = borrowingRepository.generateNewId();
        Borrowing initialBorrowing = new Borrowing(borrowingId, memberId, new ISBN("123"), LocalDate.of(2024, 1, 2), LocalDate.of(2024, 1, 23), null);
        borrowingRepository.add(initialBorrowing);


        ISBN isbn = new ISBN("isbn");
        bookRepository.register(new Book(isbn, "title", "author"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThatThrownBy(() -> borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).hasMessage("Cannot borrow book: " + BorrowingError.HAS_LATE_BORROWING);
        assertThat(borrowingRepository.findInProgressByMemberId(memberId)).hasSize(1);
    }

    @Test
    void shouldBorrowBookWhenMemberIsNotTheOneWithBorrowedBooks() {
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        MemberId memberId = memberRepository.generateNewId();
        memberRepository.add(Member.registerMember(memberId, "Jean", "Dupond", "jean.dupond@smth.com"));

        MemberId anotherMemberId = memberRepository.generateNewId();
        memberRepository.add(Member.registerMember(anotherMemberId, "Paul", "Durand", "paul.durand@smth.com"));

        List<String> isbns = List.of(
                "Book 1",
                "Book 2",
                "Book 3"
        );

        registerBooks(isbns);
        borrowBooks(isbns, anotherMemberId);


        ISBN isbn = new ISBN("isbn");
        bookRepository.register(new Book(isbn, "title", "author"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThat(borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).isEqualTo("success");
        assertThat(borrowingRepository.findInProgressByMemberId(memberId)).hasSize(1);
        assertThat(borrowingRepository.findInProgressByMemberId(anotherMemberId)).hasSize(3);
    }

    @Test
    void shouldNotBorrowBookWhenMemberIsBanned() {
        LocalDate currentDate = LocalDate.of(2024, 3, 6);
        when(timeService.getCurrentDate()).thenReturn(currentDate);

        MemberId memberId = memberRepository.generateNewId();
        memberRepository.add(new Member(memberId, "Jean", "Dupond", "jean.dupond@smth.com", MemberStatus.BANNED));

        ISBN isbn = new ISBN("isbn");
        bookRepository.register(new Book(isbn, "title", "author"));

        var command = BorrowABookCommandForTest.builder().bookIsbn("isbn").memberId(memberId.toValueString()).build();

        assertThatThrownBy(() -> borrowABookUseCase.execute(command, new BorrowABookPresenterForTest())).hasMessage("Cannot borrow book: " + BorrowingError.MEMBER_IS_BANNED);
        assertThat(borrowingRepository.findInProgressByMemberId(memberId)).hasSize(0);
    }

    private void registerBooks(List<String> isbns) {
        for (int i = 0; i < isbns.size(); i++) {
            String isbnValue = isbns.get(i);
            var book = new Book(new ISBN(isbnValue), "title " + i, "author " + i);
            bookRepository.register(book);
        }
    }

    private void borrowBooks(List<String> isbns, MemberId memberId) {
        for (String isbnValue : isbns) {
            var command = BorrowABookCommandForTest.builder().bookIsbn(isbnValue).memberId(memberId.toValueString()).build();
            var result = borrowABookUseCase.execute(command, new BorrowABookPresenterForTest());
            assertThat(result).isEqualTo("success");
        }
    }

    @Getter
    @Builder
    static class BorrowABookCommandForTest implements BorrowABookUseCase.BorrowABookCommand {
        public String bookIsbn;
        public String memberId;
    }

    static class BorrowABookPresenterForTest implements BorrowABookUseCase.BorrowABookPresenter<String> {

        @Override
        public String presentSuccess() {
            return "success";
        }

        @Override
        public String presentMemberDoesNotExist() {
            throw new RuntimeException("Member does not exist");
        }

        @Override
        public String presentBookDoesNotExist() {
            throw new RuntimeException("Book does not exist");
        }

        @Override
        public String presentCannotBorrowedBook(BorrowingError borrowingError) {
            throw new RuntimeException("Cannot borrow book: " + borrowingError);
        }
    }
}
