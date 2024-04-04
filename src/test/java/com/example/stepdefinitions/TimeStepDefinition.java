package com.example.stepdefinitions;

import com.example.test.World;
import io.cucumber.java.en.Given;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeStepDefinition {
    private final World world;

    public TimeStepDefinition(World world) {

        this.world = world;
    }

    @Given("current time is {}")
    public void currentTimeIs(String currentDateAsString) {
        var currentDate = LocalDate.parse(currentDateAsString, DateTimeFormatter.ISO_DATE);
        world.currentDate = currentDate;
        Mockito.when(world.timeService.getCurrentDate()).thenReturn(currentDate);
    }
}
