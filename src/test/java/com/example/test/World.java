package com.example.test;

import com.example.demo.core.domain.TimeService;
import com.example.demo.infrastructure.BorrowingInMemoryRepository;
import com.example.demo.infrastructure.book.BookInMemoryRepository;
import com.example.demo.infrastructure.member.MemberInMemoryRepository;
import org.mockito.Mockito;

import java.time.LocalDate;

public class World {

    public final BookInMemoryRepository bookInMemoryRepository;

    public final MemberInMemoryRepository memberInMemoryRepository;

    public final TimeService timeService;

    public LocalDate currentDate;
    public final MemberPersonas memberPersonas;
    public final BookData bookData;

    public World() {
        timeService = Mockito.mock();
        memberInMemoryRepository = new MemberInMemoryRepository();
        bookInMemoryRepository = new BookInMemoryRepository();
        memberPersonas = new MemberPersonas();
        bookData = new BookData();
    }
}
