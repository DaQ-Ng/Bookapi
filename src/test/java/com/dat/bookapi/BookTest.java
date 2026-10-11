package com.dat.bookapi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookTest{

    @Test
    void constructorStoresValues(){
        Book book = new Book("Title", "Author", 2020, 9.99, true);
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals(2020, book.getYear());
        assertEquals(9.99, book.getPrice());
        assertEquals(true, book.isAvailable());
    }
}