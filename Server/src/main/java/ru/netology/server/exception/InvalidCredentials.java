package ru.netology.server.exception;

public class InvalidCredentials extends RuntimeException{

    public InvalidCredentials(String message) {
        super(message);
    }
}
