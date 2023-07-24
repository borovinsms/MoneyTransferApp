package ru.netology.server.model;

public record ExceptionResponse(String message, int id) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ExceptionResponse response)) return false;
        return this.id == response.id && this.message.equals(response.message);
    }
}
