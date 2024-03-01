package com.example.other;

public class Price {
    int value;

    public Price(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
