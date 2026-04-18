package ru.seleznev.library.application.service;

import ru.seleznev.library.application.abstractions.BookRepository;
import ru.seleznev.library.application.service.comparators.BookComparators;
import ru.seleznev.library.domain.entities.AuthorStat;
import ru.seleznev.library.domain.entities.Book;
import ru.seleznev.library.domain.entities.LibraryStats;
import ru.seleznev.library.domain.enums.SortOption;
import ru.seleznev.library.domain.exceptions.BookNotFoundException;
import ru.seleznev.library.domain.exceptions.DuplicateBookException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class LibraryService {
    private final BookRepository repository;

    public LibraryService(BookRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null.");
        }
        this.repository = repository;
    }

    public Book addBook(String title, String author, int year) {
        String normalizedTitle = requireNonBlank(title, "Title");
        String normalizedAuthor = requireNonBlank(author, "Author");

        if (year <= 0) {
            throw new IllegalArgumentException("Year must be positive.");
        }

        boolean duplicateExists = false;
        List<Book> existingBooks = repository.findAll();

        for (Book book : existingBooks) {
            boolean sameTitle = book.getTitle().equalsIgnoreCase(normalizedTitle);
            boolean sameAuthor = book.getAuthor().equalsIgnoreCase(normalizedAuthor);

            if (sameTitle && sameAuthor) {
                duplicateExists = true;
                break;
            }
        }

        if (duplicateExists) {
            throw new DuplicateBookException("Book with the same title and author already exists.");
        }

        return repository.add(normalizedTitle, normalizedAuthor, year);
    }

    public Book removeBook(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be positive.");
        }

        Optional<Book> removedBook = repository.removeById(id);
        if (!removedBook.isPresent()) {
            throw new BookNotFoundException("Book with id=" + id + " was not found.");
        }

        return removedBook.get();
    }

    public List<Book> listBooks(SortOption sortOption) {
        SortOption option = sortOption;
        if (option == null) {
            option = SortOption.INSERTION;
        }

        List<Book> books = new ArrayList<Book>(repository.findAll());

        switch (option) {
            case TITLE:
                Collections.sort(books, BookComparators.BY_TITLE_THEN_ID);
                break;
            case AUTHOR:
                Collections.sort(books, BookComparators.BY_AUTHOR_THEN_ID);
                break;
            case YEAR:
                Collections.sort(books, BookComparators.BY_YEAR_THEN_ID);
                break;
            case INSERTION:
            default:
                break;
        }

        return books;
    }

    public List<Book> findBooks(String query) {
        String normalizedQuery = requireNonBlank(query, "Query").toLowerCase(Locale.ROOT);
        List<Book> result = new ArrayList<Book>();

        for (Book book : repository.findAll()) {
            boolean byTitle = containsIgnoreCase(book.getTitle(), normalizedQuery);
            boolean byAuthor = containsIgnoreCase(book.getAuthor(), normalizedQuery);

            if (byTitle || byAuthor) {
                result.add(book);
            }
        }

        return result;
    }

    public LibraryStats buildStats() {
        List<Book> books = repository.findAll();
        Book oldestBook = null;
        Book newestBook = null;

        for (Book book : books) {
            if (oldestBook == null || BookComparators.BY_YEAR_THEN_ID.compare(book, oldestBook) < 0) {
                oldestBook = book;
            }
            if (newestBook == null || BookComparators.BY_YEAR_THEN_ID.compare(book, newestBook) > 0) {
                newestBook = book;
            }
        }

        Optional<Book> oldestBookOptional = Optional.ofNullable(oldestBook);
        Optional<Book> newestBookOptional = Optional.ofNullable(newestBook);
        List<AuthorStat> topAuthors = collectTopAuthors(books);

        return new LibraryStats(books.size(), oldestBookOptional, newestBookOptional, topAuthors);
    }

    private static boolean containsIgnoreCase(String source, String normalizedQuery) {
        return source.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }

    private static List<AuthorStat> collectTopAuthors(List<Book> books) {
        Map<String, Long> countsByKey = new HashMap<>();
        Map<String, String> displayNameByKey = new HashMap<>();

        for (Book book : books) {
            String authorName = book.getAuthor().trim();
            String key = authorName.toLowerCase(Locale.ROOT);
            long nextCount = 1;
            if (countsByKey.containsKey(key)) {
                nextCount = countsByKey.get(key) + 1;
            }

            countsByKey.put(key, nextCount);
            if (!displayNameByKey.containsKey(key)) {
                displayNameByKey.put(key, authorName);
            }
        }

        List<AuthorStat> authorStats = new ArrayList<AuthorStat>();

        for (Map.Entry<String, Long> entry : countsByKey.entrySet()) {
            String key = entry.getKey();
            String authorName = displayNameByKey.get(key);
            Long count = entry.getValue();

            authorStats.add(new AuthorStat(authorName, count));
        }

        Collections.sort(authorStats, BookComparators.AUTHOR_STAT_BY_COUNT_DESC_THEN_AUTHOR);

        List<AuthorStat> topAuthors = new ArrayList<AuthorStat>();
        int limit = Math.min(3, authorStats.size());
        for (int i = 0; i < limit; i++) {
            topAuthors.add(authorStats.get(i));
        }

        return topAuthors;
    }
}
