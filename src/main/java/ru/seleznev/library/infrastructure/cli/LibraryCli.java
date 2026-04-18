package ru.seleznev.library.infrastructure.cli;

import ru.seleznev.library.application.service.LibraryService;
import ru.seleznev.library.domain.entities.LibraryStats;
import ru.seleznev.library.domain.entities.Book;
import ru.seleznev.library.domain.enums.SortOption;
import ru.seleznev.library.domain.exceptions.BookNotFoundException;
import ru.seleznev.library.domain.exceptions.DuplicateBookException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class LibraryCli {
    private final LibraryService libraryService;
    private final Scanner scanner;
    private final PrintStream out;

    public LibraryCli(LibraryService libraryService, InputStream in, PrintStream out) {
        if (libraryService == null) {
            throw new IllegalArgumentException("Library service must not be null.");
        }
        if (in == null) {
            throw new IllegalArgumentException("Input stream must not be null.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Output stream must not be null.");
        }

        this.libraryService = libraryService;
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void run() {
        out.println("Library CLI started.");
        out.println("Commands: ADD, REMOVE, LIST, FIND, STATS, EXIT");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            try {
                if (handleCommand(input)) {
                    out.println("Bye.");
                    return;
                }
            } catch (DuplicateBookException | BookNotFoundException | IllegalArgumentException ex) {
                out.println("ERROR: " + ex.getMessage());
            }
        }
    }

    boolean handleCommand(String input) {
        String[] tokens = input.split("\\s+", 2);
        String command = tokens[0].toUpperCase(Locale.ROOT);
        String args = tokens.length > 1 ? tokens[1].trim() : "";
        boolean shouldExit = false;

        switch (command) {
            case "ADD":
                handleAdd(args);
                break;
            case "REMOVE":
                handleRemove(args);
                break;
            case "LIST":
                handleList(args);
                break;
            case "FIND":
                handleFind(args);
                break;
            case "STATS":
                handleStats(args);
                break;
            case "EXIT":
                requireNoArgs(args, "EXIT");
                shouldExit = true;
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + command);
        }

        return shouldExit;
    }

    private void handleAdd(String args) {
        String[] parts = args.split(";", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("ADD format: ADD <title>;<author>;<year>");
        }

        String title = parts[0].trim();
        String author = parts[1].trim();
        int year = parseInt(parts[2].trim(), "Year must be an integer.");

        Book added = libraryService.addBook(title, author, year);
        out.println("Added: " + formatBook(added));
    }

    private void handleRemove(String args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("REMOVE format: REMOVE <id>");
        }

        long id;
        try {
            id = Long.parseLong(args);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Id must be an integer.");
        }

        Book removed = libraryService.removeBook(id);
        out.println("Removed: " + formatBook(removed));
    }

    private void handleList(String args) {
        SortOption sortOption;
        if (args.isEmpty()) {
            sortOption = SortOption.INSERTION;
        } else {
            if (args.contains(" ")) {
                throw new IllegalArgumentException("LIST format: LIST [title|author|year]");
            }
            sortOption = parseSortOption(args);
        }

        List<Book> books = libraryService.listBooks(sortOption);
        printBooks(books);
    }

    private void handleFind(String args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("FIND format: FIND <query>");
        }

        List<Book> books = libraryService.findBooks(args);
        printBooks(books);
    }

    private void handleStats(String args) {
        requireNoArgs(args, "STATS");
        LibraryStats stats = libraryService.buildStats();

        out.println("Total books: " + stats.getTotalBooks());

        if (stats.getOldestBook().isPresent()) {
            out.println("Oldest book: " + formatBook(stats.getOldestBook().get()));
        } else {
            out.println("Oldest book: -");
        }

        if (stats.getNewestBook().isPresent()) {
            out.println("Newest book: " + formatBook(stats.getNewestBook().get()));
        } else {
            out.println("Newest book: -");
        }

        if (stats.getTopAuthors().isEmpty()) {
            out.println("Top authors: -");
            return;
        }

        out.println("Top authors:");
        for (int i = 0; i < stats.getTopAuthors().size(); i++) {
            out.println((i + 1) + ". " + stats.getTopAuthors().get(i).getAuthor() + " (" + stats.getTopAuthors().get(i).getCount() + ")");
        }
    }

    private void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            out.println("No books found.");
            return;
        }

        for (Book book : books) {
            out.println(formatBook(book));
        }
    }

    private static int parseInt(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void requireNoArgs(String args, String command) {
        if (!args.isEmpty()) {
            throw new IllegalArgumentException(command + " does not accept arguments.");
        }
    }

    private static SortOption parseSortOption(String token) {
        String normalized = token.toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "title":
                return SortOption.TITLE;
            case "author":
                return SortOption.AUTHOR;
            case "year":
                return SortOption.YEAR;
            default:
                throw new IllegalArgumentException("Unsupported sort option: " + token);
        }
    }

    private String formatBook(Book book) {
        return "[id=" + book.getId() + "] " + book.getTitle() + " - " + book.getAuthor() + " (" + book.getYear() + ")";
    }
}
