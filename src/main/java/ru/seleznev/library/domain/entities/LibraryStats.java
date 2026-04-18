package ru.seleznev.library.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class LibraryStats {
    private int totalBooks;
    private Optional<Book> oldestBook;
    private Optional<Book> newestBook;
    private List<AuthorStat> topAuthors;
}
