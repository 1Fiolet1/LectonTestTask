package ru.seleznev.library.application.service.comparators;

import ru.seleznev.library.domain.entities.AuthorStat;
import ru.seleznev.library.domain.entities.Book;

import java.util.Comparator;
import java.util.Locale;

public class BookComparators {
    public static final Comparator<Book> BY_YEAR_THEN_ID = new Comparator<Book>() {
        @Override
        public int compare(Book first, Book second) {
            int yearCompare = Integer.compare(first.getYear(), second.getYear());
            if (yearCompare != 0) {
                return yearCompare;
            }
            return Long.compare(first.getId(), second.getId());
        }
    };

    public static final Comparator<Book> BY_TITLE_THEN_ID = new Comparator<Book>() {
        @Override
        public int compare(Book first, Book second) {
            String firstTitle = first.getTitle().toLowerCase(Locale.ROOT);
            String secondTitle = second.getTitle().toLowerCase(Locale.ROOT);
            int titleCompare = firstTitle.compareTo(secondTitle);
            if (titleCompare != 0) {
                return titleCompare;
            }
            return Long.compare(first.getId(), second.getId());
        }
    };

    public static final Comparator<Book> BY_AUTHOR_THEN_ID = new Comparator<Book>() {
        @Override
        public int compare(Book first, Book second) {
            String firstAuthor = first.getAuthor().toLowerCase(Locale.ROOT);
            String secondAuthor = second.getAuthor().toLowerCase(Locale.ROOT);
            int authorCompare = firstAuthor.compareTo(secondAuthor);
            if (authorCompare != 0) {
                return authorCompare;
            }
            return Long.compare(first.getId(), second.getId());
        }
    };

    public static final Comparator<AuthorStat> AUTHOR_STAT_BY_COUNT_DESC_THEN_AUTHOR = new Comparator<AuthorStat>() {
        @Override
        public int compare(AuthorStat first, AuthorStat second) {
            int countCompare = Long.compare(second.getCount(), first.getCount());
            if (countCompare != 0) {
                return countCompare;
            }

            String firstAuthor = first.getAuthor().toLowerCase(Locale.ROOT);
            String secondAuthor = second.getAuthor().toLowerCase(Locale.ROOT);
            return firstAuthor.compareTo(secondAuthor);
        }
    };

    private BookComparators() {
    }
}
