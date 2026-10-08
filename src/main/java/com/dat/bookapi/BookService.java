package com.dat.bookapi;

import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class BookService{
    private int nextId = 1;
    private ArrayList<Book> books = new ArrayList<>();

    public BookService(){
        // Initialize some books for demonstration purposes
        addBook(new Book("Title1", "Author1", 2020, 19.99, true));
        addBook(new Book("Title2", "Author2", 2021, 29.99, false));
    }

    public ArrayList<Book> getAllBooks(){
        return books;
    }

    public Book addBook(Book book){
        book.setId(nextId++);
        books.add(book);
        return book;
    }

    public Book getBookById(int id){
        for(int index = 0; index < books.size(); index++){
            if(books.get(index).getId() == id){
                return books.get(index);
            }
        }
        return null; // Book not found
    }

    public Book deleteBook(int id){
        for(int index = 0; index < books.size(); index++){
            if(books.get(index).getId() == id){
                return books.remove(index);
            }
        }
        return null; // Book not found
    }

    public Book updateBook(int id, Book updatedBook){
        for(int index = 0; index < books.size(); index++){
            if(books.get(index).getId() == id){
                books.set(index, updatedBook);
                books.get(index).setId(id);
                return updatedBook;
            }
        }
        return null; // Book not found
    }
}