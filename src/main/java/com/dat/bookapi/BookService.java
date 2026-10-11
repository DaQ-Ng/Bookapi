package com.dat.bookapi;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService{
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book addBook(Book book){
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
}