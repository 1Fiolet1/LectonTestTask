package ru.seleznev.library.domain.exceptions;

public final class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
