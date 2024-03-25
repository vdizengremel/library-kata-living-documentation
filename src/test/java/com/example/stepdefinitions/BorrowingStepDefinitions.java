package com.example.stepdefinitions;

import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingError;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.usecases.BorrowABookUseCase;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import io.cucumber.java.en.And;
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

import static org.assertj.core.api.Assertions.assertThat;

public class BorrowingStepDefinitions {
    private final World world;
    private BorrowingInMemoryRepository borrowingInMemoryRepository;
    private PresenterException thrownException;

    public BorrowingStepDefinitions(World world) {
        this.world = world;
    }


    @Given("next generated borrowing ids will be:")
    public void nextGeneratedBorrowingIdsWillBe(List<String> uuids) {
        var borrowingIds = uuids.stream().map(BorrowingId::fromString).toList();
        borrowingInMemoryRepository = new BorrowingInMemoryRepository(borrowingIds);
    }

    @When("member with id {} borrow the book with ISBN {}")
    public void memberWithIdBorrowTheBookWithISBN(String memberId, String isbn) {
        try {
            borrowABook(memberId, isbn);
        } catch (PresenterException e) {
            thrownException = e;
        }
    }

    @Then("borrowing should be saved with:")
    public void borrowingShouldBeSavedWith(List<Map<String, String>> borrowings) {
        var borrowingData = borrowings.getFirst();
        var borrowingId = BorrowingId.fromString(borrowingData.get("id"));

        Optional<Borrowing> optionalBorrowing = borrowingInMemoryRepository.findById(borrowingId);
        assertThat(optionalBorrowing).isPresent();
        Borrowing expectedBorrowing = new Borrowing(borrowingId, MemberId.from(borrowingData.get("member id")), new ISBN(borrowingData.get("isbn")), LocalDate.parse(borrowingData.get("start date"), DateTimeFormatter.ISO_DATE), LocalDate.parse(borrowingData.get("max authorized return date"), DateTimeFormatter.ISO_DATE), null);
        assertThat(optionalBorrowing.get()).usingRecursiveComparison().isEqualTo(expectedBorrowing);
    }

    @Given("member with id {} has already borrowed following books with ISBN:")
    public void memberWithIdCAcaCCDdAHasAlreadyBorrowedFollowingBooksWithISBN(String memberId, List<String> isbns) {
        isbns.forEach(isbn -> borrowABook(memberId, isbn));
    }

    @Then("the borrowing result should be an error indicating {}")
    public void theBorrowingResultShouldBeAnErrorIndicatingTheMemberAsReachedTheMaximumAmountOfBorrowedBooks(String expectedMessage) {
        assertThat(thrownException).hasMessage(expectedMessage);
    }

    private void borrowABook(String memberId, String isbn) {
        BorrowABookUseCase borrowABookUseCase = new BorrowABookUseCase(world.memberInMemoryRepository, world.bookInMemoryRepository, borrowingInMemoryRepository, world.timeService);
        borrowABookUseCase.execute(new BorrowABookCommandForTest(isbn, memberId), new BorrowABookPresenterForTest());
    }

    @Given("member with id {} has already borrowed following books at {} with ISBN:")
    public void memberWithIdCAcaCCDdAHasAlreadyBorrowedFollowingBooksAtWithISBN(String memberId, String borrowingDateAsString, List<String> isbns) {
        var borrowingDate = LocalDate.parse(borrowingDateAsString, DateTimeFormatter.ISO_DATE);
        Mockito.when(world.timeService.getCurrentDate()).thenReturn(borrowingDate, world.currentDate);
        isbns.forEach(isbn -> this.borrowABook(memberId, isbn));
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
}
