package com.dat.bookapi;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService{
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
        // Initialize some books for demonstration purposes
        if(bookRepository.count() == 0){
            addBook(new Book("Title1", "Author1", 2020, 19.99, true));
            addBook(new Book("Title2", "Author2", 2021, 29.99, false));
        }
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