package com.dat.bookapi.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.security.Principal;


@RestController
public class BookController{
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }
    
    @GetMapping("/books")
    public Page<Book> getBooks(@RequestParam(defaultValue = "") String title, Pageable pageable){
        return bookService.searchBooks(title, pageable);
    }

    @GetMapping("/books/{id}")
    public Book getBookById(@PathVariable int id){
        Book getBook = bookService.getBookById(id);
        if(getBook != null){
            return getBook;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found.");
    }

    @PostMapping("/books")
    public Book addBook(@Valid @RequestBody Book book, Principal principal){
        return bookService.addBook(book, principal.getName());
    }

    @DeleteMapping("/books/{id}")
    public Book deleteBook(@PathVariable int id, Principal principal){
        return bookService.deleteBook(id, principal.getName());
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable int id, @Valid @RequestBody Book updatedBook, Principal principal){
        return bookService.updateBook(id, updatedBook, principal.getName());
    }
}