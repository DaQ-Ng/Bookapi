package com.dat.bookapi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class BookServiceTest{

    @Test
    void updateMissingIdReturnsNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.existsById(99)).thenReturn(false);
        BookService service = new BookService(repo);

        Book result = service.updateBook(99, new Book("Title", "Author", 2020, 9.99, true));

        assertNull(result);

        verify(repo,never()).save(any(Book.class));
    }
}