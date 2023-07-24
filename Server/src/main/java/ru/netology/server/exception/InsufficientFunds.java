package ru.netology.server.exception;

public class InsufficientFunds extends RuntimeException{

    public InsufficientFunds(String message) {
        super(message);
    }
}
