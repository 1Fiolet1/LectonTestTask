package ru.seleznev;

import ru.seleznev.library.application.service.LibraryService;
import ru.seleznev.library.infrastructure.cli.LibraryCli;
import ru.seleznev.library.infrastructure.inmemory.InMemoryBookRepository;

public class App {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService(new InMemoryBookRepository());
        new LibraryCli(libraryService, System.in, System.out).run();
    }
}
