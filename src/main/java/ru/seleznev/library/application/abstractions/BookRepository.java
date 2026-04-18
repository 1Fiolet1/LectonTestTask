package ru.seleznev.library.application.abstractions;

import ru.seleznev.library.domain.entities.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book add(String title, String author, int year);

    Optional<Book> removeById(long id);

    List<Book> findAll();
}
