package ru.seleznev.library.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Book {
    private long id;
    private String title;
    private String author;
    private int year;
}
