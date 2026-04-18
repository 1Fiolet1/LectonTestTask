package ru.seleznev.library.infrastructure.inmemory;

import ru.seleznev.library.application.abstractions.BookRepository;
import ru.seleznev.library.domain.entities.Book;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> booksById = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public synchronized Book add(String title, String author, int year) {
        Book book = new Book(nextId++, title, author, year);
        booksById.put(book.getId(), book);
        return book;
    }

    @Override
    public synchronized Optional<Book> removeById(long id) {
        return Optional.ofNullable(booksById.remove(id));
    }

    @Override
    public synchronized List<Book> findAll() {
        return new ArrayList<Book>(booksById.values());
    }
}
