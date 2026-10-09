package com.dat.bookapi;

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

@RestController
public class BookController{
    private final BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }
    
    @GetMapping("/books")
    public List<Book> getBooks(){
        return bookService.getAllBooks();
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
    public Book addBook(@Valid @RequestBody Book book){
        return bookService.addBook(book);
    }

    @DeleteMapping("/books/{id}")
    public Book deleteBook(@PathVariable int id){
        Book deletedBook = bookService.deleteBook(id);
        if(deletedBook != null){
            return deletedBook;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found.");
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable int id,@Valid @RequestBody Book updatedBook){
        Book updatedBookResult = bookService.updateBook(id, updatedBook);
        if(updatedBookResult != null){
            return updatedBookResult;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found.");
    }
}