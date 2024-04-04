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
    }

    public BookInMemoryRepository bookInMemoryRepository;

    public MemberInMemoryRepository memberInMemoryRepository;

    public BorrowingInMemoryRepository borrowingInMemoryRepository;
    public TimeService timeService;
    public LocalDate currentDate;
}
