package com.dat.bookapi.book;

import com.dat.bookapi.user.User;
import com.dat.bookapi.user.UserRepository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

class BookServiceTest{

    @Test
    void updateMissingIdReturnsNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.existsById(99)).thenReturn(false); //set up rule a head
        BookService service = new BookService(repo, mock(UserRepository.class));

        Book result = service.updateBook(99, new Book("Title", "Author", 2020, 9.99, true));

        assertNull(result);

        verify(repo,never()).save(any(Book.class));
    }

    @Test
    void updateExistingIdSavesBook(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.existsById(1)).thenReturn(true); //set up rule a head
        BookService service = new BookService(repo, mock(UserRepository.class));

        Book bookToUpdate = new Book("Title", "Author", 2020, 9.99, true);
        when(repo.save(bookToUpdate)).thenReturn(bookToUpdate); //set up rule a head

        Book result = service.updateBook(1, bookToUpdate);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(repo, times(1)).save(bookToUpdate);
    }

    @Test 
    void deleteMissingIdThrows404(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.findById(99)).thenReturn(Optional.empty()); //set up rule a head
        BookService service = new BookService(repo, mock(UserRepository.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.deleteBook(99, "tam@test.com"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(repo, never()).deleteById(anyInt());
    }

    @Test 
    void deleteExistingIdReturnsBook(){
        BookRepository repo = mock(BookRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        Book bookToDelete = new Book("Title", "Author", 2020, 9.99, true);
        bookToDelete.setSeller(tam); // Tam owns this book
        when(repo.findById(1)).thenReturn(Optional.of(bookToDelete));
        BookService service = new BookService(repo, mock(UserRepository.class));

        Book result = service.deleteBook(1, "tam@test.com");

        assertEquals(bookToDelete, result);
        verify(repo, times(1)).deleteById(1);
    }

    @Test 
    void deleteOtherSellersBookThrows403(){
        BookRepository repo = mock(BookRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        Book bookToDelete = new Book("Title", "Author", 2020, 9.99, true);
        bookToDelete.setSeller(tam); // Tam owns this book
        when(repo.findById(1)).thenReturn(Optional.of(bookToDelete));
        BookService service = new BookService(repo, mock(UserRepository.class));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.deleteBook(1, "minh@test.com"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(repo, never()).deleteById(anyInt());
    }

    @Test
    void getMissingIdReturnNull(){
        BookRepository repo = mock(BookRepository.class);
        when(repo.findById(99)).thenReturn(Optional.empty());
        BookService service = new BookService(repo, mock(UserRepository.class));

        Book result = service.getBookById(99);

        assertNull(result);
    }

    @Test
    void getExistingIdReturnsBook(){
        BookRepository repo = mock(BookRepository.class);
        Book bookToGet = new Book("Title", "Author", 2020, 9.99, true);
        when(repo.findById(1)).thenReturn(Optional.of(bookToGet));
        BookService service = new BookService(repo, mock(UserRepository.class));

        Book result = service.getBookById(1);

        assertEquals(bookToGet, result);
    }

    @Test
    void addBookIgnoresIdFromBody(){
        BookRepository repo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        when(userRepo.findByEmailIgnoreCase("tam@test.com")).thenReturn(Optional.of(tam)); // box with Tam inside
        BookService service = new BookService(repo, userRepo);

        Book bookToAdd = new Book("Title", "Author", 2020, 9.99, true);
        bookToAdd.setId(5);

        service.addBook(bookToAdd, "tam@test.com");
        assertNull(bookToAdd.getId());
    }

    @Test
    void addBookUnknownEmailThrows401(){
        BookRepository repo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findByEmailIgnoreCase("ghost@test.com")).thenReturn(Optional.empty()); // empty box
        BookService service = new BookService(repo, userRepo);

        Book bookToAdd = new Book("Title", "Author", 2020, 9.99, true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.addBook(bookToAdd, "ghost@test.com"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(repo, never()).save(any(Book.class));
    }

    @Test
    void addBookSellerComesFromEmailNotBody(){                                  
        BookRepository repo = mock(BookRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        User tam = new User("Tam", "tam@test.com", "hash");
        User minh = new User("Minh", "minh@test.com", "hash");
        when(userRepo.findByEmailIgnoreCase("tam@test.com")).thenReturn(Optional.of(tam));
        BookService service = new BookService(repo, userRepo);

        Book bookToAdd = new Book("Title", "Author", 2020, 9.99, true);
        bookToAdd.setSeller(minh);                                                

        service.addBook(bookToAdd, "tam@test.com");                              

        assertEquals(tam, bookToAdd.getSeller());                                 
    }
}