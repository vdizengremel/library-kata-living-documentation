package com.example.test;

import com.example.demo.core.domain.TimeService;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import org.mockito.Mockito;

import java.time.LocalDate;

public class World {

    public World() {
        timeService = Mockito.mock();
        memberInMemoryRepository = new MemberInMemoryRepository();
        bookInMemoryRepository = new BookInMemoryRepository();
    }

    public final BookInMemoryRepository bookInMemoryRepository;

    public final MemberInMemoryRepository memberInMemoryRepository;

    public final TimeService timeService;
    public LocalDate currentDate;
}
