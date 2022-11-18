package ru.practicum.ewmmain.exception;

public class ConflictError extends RuntimeException {
    public ConflictError(String message) {
        super(message);
    }
}
