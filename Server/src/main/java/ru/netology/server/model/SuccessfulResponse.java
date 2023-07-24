package ru.netology.server.model;

public record SuccessfulResponse(String operationId) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SuccessfulResponse response)) return false;
        return this.operationId.equals(response.operationId);
    }
}
