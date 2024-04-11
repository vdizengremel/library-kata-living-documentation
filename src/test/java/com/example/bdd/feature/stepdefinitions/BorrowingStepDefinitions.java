package com.example.bdd.feature.stepdefinitions;

import com.example.demo.core.domain.TimeService;
import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingError;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.usecases.BorrowABookUseCase;
import com.example.demo.core.usecases.ReturnABookUseCase;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import com.example.bdd.BookData;
import com.example.bdd.MemberPersonas;
import com.example.bdd.feature.PresenterException;
import com.example.bdd.feature.World;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Builder;
import lombok.Getter;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.bdd.feature.CucumberUtils.catchPresenterException;
import static org.assertj.core.api.Assertions.assertThat;

public class BorrowingStepDefinitions {
    private final World world;
    private final BorrowingInMemoryRepository borrowingInMemoryRepository;
    private final TimeService timeService;
    private final MemberPersonas memberPersonas;
    private final BookData bookData;

    private PresenterException thrownException;


    public BorrowingStepDefinitions(World world) {
        this.world = world;
        this.timeService = world.timeService;
        memberPersonas = world.memberPersonas;
        bookData = world.bookData;
        borrowingInMemoryRepository = new BorrowingInMemoryRepository();
    }

    @When("{} borrows {}")
    public void memberBorrowsTheBook(String memberName, String bookName) {
        String isbn;
        if (bookName.contains("unknown")) {
            isbn = "1112223333";
        } else {
            isbn = bookData.getISBNFor(bookName);
        }

        String memberId;

        if ("unknown member".equals(memberName)) {
            memberId = UUID.randomUUID().toString();
        } else {
            memberId = memberPersonas.getIdFor(memberName);
        }

        thrownException = catchPresenterException(() -> borrowABook(memberId, isbn));
    }

    @Then("{} should have current borrowing(s):")
    public void memberShouldHaveCurrentBorrowings(String memberName, List<Map<String, String>> borrowingsData) {
        var memberId = MemberId.from(memberPersonas.getIdFor(memberName));
        List<Borrowing> inProgressByMemberId = borrowingInMemoryRepository.findInProgressByMemberId(memberId);

        var expectedBorrowings = borrowingsData.stream().map(borrowingData -> mapRowToBorrowing(memberId, borrowingData)).toList();

        assertThat(inProgressByMemberId)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(expectedBorrowings.toArray(Borrowing[]::new));
    }

    public Borrowing mapRowToBorrowing(MemberId memberId, Map<String, String> borrowingData) {
        var borrowingId = BorrowingId.fromString(UUID.randomUUID().toString());
        LocalDate returnDate = stringToLocalDate(borrowingData.get("return date"));

        return new Borrowing(borrowingId, memberId, new ISBN(bookData.getISBNFor(borrowingData.get("title"))), stringToLocalDate(borrowingData.get("start date")), stringToLocalDate(borrowingData.get("max authorized return date")), returnDate);
    }

    private LocalDate stringToLocalDate(String date) {
        return Optional.ofNullable(date).map(data -> LocalDate.parse(data, DateTimeFormatter.ISO_DATE)).orElse(null);
    }

    @Given("{} has already borrowed:")
    public void memberWithIdHasAlreadyBorrowedFollowingBooks(String memberName, List<String> bookNames) {
        var memberId = memberPersonas.getIdFor(memberName);
        bookNames.stream().map(bookData::getISBNFor).forEach(isbn -> borrowABook(memberId, isbn));
    }

    @Then("the borrowing should fail because {}")
    public void theBorrowingResultShouldBeAnErrorIndicatingTheMemberAsReachedTheMaximumAmountOfBorrowedBooks(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    private void borrowABook(String memberId, String isbn) {
        BorrowABookUseCase borrowABookUseCase = new BorrowABookUseCase(world.memberInMemoryRepository, world.bookInMemoryRepository, borrowingInMemoryRepository, timeService);
        borrowABookUseCase.execute(new BorrowABookCommandForTest(isbn, memberId), new BorrowABookPresenterForTest());
    }

    @Given("{} has already borrowed following books at {}:")
    public void memberHasAlreadyBorrowedFollowingBooksAt(String memberName, String borrowingDateAsString, List<String> bookNames) {
        var borrowingDate = LocalDate.parse(borrowingDateAsString, DateTimeFormatter.ISO_DATE);
        Mockito.when(timeService.getCurrentDate()).thenReturn(borrowingDate);

        var memberId = memberPersonas.getIdFor(memberName);
        bookNames.stream().map(bookData::getISBNFor).forEach(isbn -> this.borrowABook(memberId, isbn));
        Mockito.when(timeService.getCurrentDate()).thenReturn(world.currentDate);
    }

    @When("{} is returned")
    public void bookIsReturned(String bookName) {
        var isbn = bookData.getISBNFor(bookName);
        ReturnABookUseCase returnABookUseCase = new ReturnABookUseCase(borrowingInMemoryRepository, timeService);
        thrownException = catchPresenterException(() -> returnABookUseCase.execute(() -> isbn, new ReturnABookPresenterForTest()));
    }

    @Then("returning the book should fail because {}")
    public void returningTheBookShouldFailBecauseNoBorrowingExistsForThisBook(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    @Then("{} should have past borrowings:")
    public void janeShouldHavePastBorrowings(String memberName, List<Map<String, String>> pastBorrowingsData) {
        var memberId = MemberId.from(memberPersonas.getIdFor(memberName));
        List<Borrowing> finishedByMemberId = borrowingInMemoryRepository.findFinishedByMemberId(memberId);
        var expectedBorrowings = pastBorrowingsData.stream().map(borrowingData -> mapRowToBorrowing(memberId, borrowingData)).toList();

        assertThat(finishedByMemberId)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(expectedBorrowings.toArray(Borrowing[]::new));
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
            throw new PresenterException("member does not exist");
        }

        @Override
        public String presentBookDoesNotExist() {
            throw new PresenterException("book does not exist");
        }

        @Override
        public String presentCannotBorrowedBook(BorrowingError borrowingError) {
            String errorCause = switch (borrowingError) {
                case MEMBER_IS_BANNED -> null;
                case HAS_LATE_BORROWING -> "member has not returned a book in time";
                case HAS_REACHED_MAX_AUTHORIZED_BORROWING -> "member has reached the maximum amount of borrowed books";
            };

            throw new PresenterException(errorCause);
        }
    }

    static class ReturnABookPresenterForTest implements ReturnABookUseCase.ReturnABookPresenter<Void> {

        @Override
        public Void presentBookReturned() {
            return null;
        }

        @Override
        public Void presentBorrowingDoesNotExist() {
            throw new PresenterException("no borrowing exists for this book");
        }
    }
}
