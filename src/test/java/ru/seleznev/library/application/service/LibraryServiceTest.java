package ru.seleznev.library.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.seleznev.library.domain.entities.LibraryStats;
import ru.seleznev.library.domain.exceptions.DuplicateBookException;
import ru.seleznev.library.infrastructure.inmemory.InMemoryBookRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryServiceTest {
    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(new InMemoryBookRepository());
    }

    @Test
    void rejectsDuplicateByTitleAndAuthorIgnoringCase() {
        libraryService.addBook("book1", "author1", 2008);

        assertThrows(DuplicateBookException.class,
                () -> libraryService.addBook(" BOOK1 ", "AUTHOR1", 2008));
    }

    @Test
    void findsByTitleOrAuthorIgnoringCase() {
        libraryService.addBook("book1", "author1", 2003);
        libraryService.addBook("book2", "author2", 2017);
        libraryService.addBook("book3", "author11", 1999);

        assertEquals(2, libraryService.findBooks("AUTHOR1").size());
        assertEquals(1, libraryService.findBooks("BOOK2").size());
    }

    @Test
    void buildsStatsWithOldestNewestAndTopAuthors() {
        libraryService.addBook("book1", "author1", 1949);
        libraryService.addBook("book2", "author1", 1945);
        libraryService.addBook("book3", "author1", 1938);
        libraryService.addBook("book4", "author2", 1951);
        libraryService.addBook("book5", "author3", 1965);

        LibraryStats stats = libraryService.buildStats();

        assertEquals(5, stats.getTotalBooks());
        assertTrue(stats.getOldestBook().isPresent());
        assertTrue(stats.getNewestBook().isPresent());
        assertEquals(1938, stats.getOldestBook().get().getYear());
        assertEquals(1965, stats.getNewestBook().get().getYear());
        assertEquals(3, stats.getTopAuthors().size());
        assertEquals("author1", stats.getTopAuthors().get(0).getAuthor());
        assertEquals(3, stats.getTopAuthors().get(0).getCount());
    }
}
