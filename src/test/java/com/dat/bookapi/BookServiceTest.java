package com.dat.bookapi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

class BookServiceTest{

    @Test
    void updateMissingIdReturnsNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.existsById(99)).thenReturn(false); //set up rule a head
        BookService service = new BookService(repo);

        Book result = service.updateBook(99, new Book("Title", "Author", 2020, 9.99, true));

        assertNull(result);

        verify(repo,never()).save(any(Book.class));
    }

    @Test
    void updateExistingIdSavesBook(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.existsById(1)).thenReturn(true); //set up rule a head
        BookService service = new BookService(repo);

        Book bookToUpdate = new Book("Title", "Author", 2020, 9.99, true);
        when(repo.save(bookToUpdate)).thenReturn(bookToUpdate); //set up rule a head

        Book result = service.updateBook(1, bookToUpdate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(repo, times(1)).save(bookToUpdate);
    }

    @Test 
    void deleteMissingIdReturnsNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.findById(99)).thenReturn(Optional.empty()); //set up rule a head
        BookService service = new BookService(repo);

        Book result = service.deleteBook(99);

        assertNull(result);
        verify(repo, never()).deleteById(anyInt());
    }

    @Test 
    void deleteExistingIdReturnsBook(){
        BookRepository repo = mock(BookRepository.class);
        Book bookToDelete = new Book("Title", "Author", 2020, 9.99, true);
        when(repo.findById(1)).thenReturn(Optional.of(bookToDelete));
        BookService service = new BookService(repo);

        Book result = service.deleteBook(1);

        assertEquals(bookToDelete, result);
        verify(repo, times(1)).deleteById(1);
    }

    @Test
    void getMissingIdReturnNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.findById(99)).thenReturn(Optional.empty());
        BookService service = new BookService(repo);

        Book result = service.getBookById(99);

        assertNull(result);
    }

    @Test
    void getExistingIdReturnsBook(){
        BookRepository repo = mock(BookRepository.class);
        Book bookToGet = new Book("Title", "Author", 2020, 9.99, true);
        when(repo.findById(1)).thenReturn(Optional.of(bookToGet));
        BookService service = new BookService(repo);

        Book result = service.getBookById(1);

        assertEquals(bookToGet, result);
    }

    @Test
    void addBookIgnoresIdFromBody(){
        BookRepository repo = mock(BookRepository.class);
        Book bookToAdd = new Book("Title", "Author", 2020, 9.99, true);
        bookToAdd.setId(5);
        BookService service = new BookService(repo);

        Book result = service.addBook(bookToAdd);
        assertNull(bookToAdd.getId());
    }
}