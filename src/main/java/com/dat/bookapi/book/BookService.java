package com.dat.bookapi;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class BookService{
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository){
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book addBook(Book book, String email){
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist"));
        book.setSeller(user);
        book.setId(null);
        return bookRepository.save(book);
    }

    public Book getBookById(int id){
        return bookRepository.findById(id).orElse(null);
    }

    public Book deleteBook(int id){
        Book bookToDelete = bookRepository.findById(id).orElse(null);
        if(bookToDelete != null){
            bookRepository.deleteById(id);
            return bookToDelete;
        }
        return null; // Book not found
    }

    public Book updateBook(int id, Book updatedBook){
        if(bookRepository.existsById(id)){
            updatedBook.setId(id);
            return bookRepository.save(updatedBook);
        }
        return null; // Book not found
    }

    public Page<Book> searchBooks(String title, Pageable pageable){
        return bookRepository.findByTitleContainingIgnoreCaseAndAvailableTrue(title, pageable);
    }
}