package ru.netology.server.model;

public class Amount {

    private final String currency;
    private final int value;

    public Amount(String currency, int value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public int getValue() {
        return value;
    }
}
