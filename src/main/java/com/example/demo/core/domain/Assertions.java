package com.example.demo.core.domain;

import java.time.LocalDate;

public class Assertions {
    public static void assertNotNull(Object object, String fieldName) {
        if(object == null) {
            throw new AssertionError(fieldName + " should not be null");
        }
    }

    public static void assertNotNullOrEmpty(String string, String fieldName) {
        if(string == null || string.isEmpty()) {
            throw new AssertionError(fieldName + " should not be null or empty");
        }
    }

    public static void assertDateIsAfter(LocalDate shouldBeAfter, LocalDate shouldBeBefore, String afterFieldName, String beforeFieldName) {
        if(shouldBeAfter.isBefore(shouldBeBefore) || shouldBeAfter.isEqual(shouldBeBefore)) {
            throw new AssertionError(afterFieldName + " should be after "+ beforeFieldName);
        }
    }

}
