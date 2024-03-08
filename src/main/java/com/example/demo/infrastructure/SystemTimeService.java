package com.example.demo.infrastructure;

import com.example.demo.core.domain.TimeService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SystemTimeService implements TimeService {
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
