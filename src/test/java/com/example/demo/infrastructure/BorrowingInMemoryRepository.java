package com.example.demo.infrastructure;

import com.example.demo.core.domain.book.ISBN;
import com.example.demo.core.domain.member.MemberId;
import com.example.demo.core.domain.member.Borrowing;
import com.example.demo.core.domain.member.BorrowingId;
import com.example.demo.core.domain.member.BorrowingRepository;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Profile("inMemoryRepository")
public class BorrowingInMemoryRepository extends AbstractInMemoryRepository<BorrowingId, Borrowing> implements BorrowingRepository {

    public static final List<BorrowingId> IDS = Stream.of(
                    "ce9140dc-bf8b-47b8-884a-e9385b272b73",
                    "cc5f3dfe-b8da-4f36-ab24-ee60542f329e",
                    "bc9eafb3-c38f-4f7f-9dcc-64300d2648a0",
                    "975db8f0-1fd9-4f8c-9dcc-fdeabc3eacdf",
                    "0524f1fb-cfe0-41fe-ad83-bc804779377d",
                    "21acf88c-8981-47b0-9603-03377b348698",
                    "625dd772-389a-4f41-82d4-fb3636259b49"
            )
            .map(UUID::fromString)
            .map(BorrowingId::new)
            .toList();

    public BorrowingInMemoryRepository() {
        super(IDS);
    }

    public BorrowingInMemoryRepository(List<BorrowingId> borrowingIds) {
        super(borrowingIds);
    }

    @Override
    public void add(Borrowing borrowing) {
        this.add(borrowing.getId(), borrowing);
    }

    @Override
    public void update(Borrowing borrowing) {
        this.update(borrowing.getId(), borrowing);
    }

    @Override
    public List<Borrowing> findInProgressByMemberId(MemberId memberId) {
        return streamData()
                .filter(borrowing -> borrowing.isMadeBy(memberId))
                .filter(Borrowing::isInProgress).toList();
    }

    protected Borrowing copy(Borrowing borrowing) {
        final BorrowingData borrowingData = new BorrowingData();
        borrowing.provideInterest(borrowingData);

        return new Borrowing(BorrowingId.fromString(borrowingData.id), MemberId.from(borrowingData.memberId), new ISBN(borrowingData.isbn), borrowingData.startDate, borrowingData.maxAuthorizedReturnDate, borrowingData.returnDate);
    }

    @Setter
    public static class BorrowingData implements Borrowing.BorrowingInterest {
        public String id;
        public String isbn;
        public String memberId;
        public LocalDate startDate;
        public LocalDate maxAuthorizedReturnDate;
        public LocalDate returnDate;
    }
}
