package ru.seleznev.library.domain.exceptions;

public final class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}
