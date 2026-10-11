package com.dat.bookapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner{
    private final BookRepository bookRepository;

    public DataSeeder(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args){
        if(bookRepository.count() == 0){
            bookRepository.save(new Book("Title1", "Author1", 2020, 19.99, true));
            bookRepository.save(new Book("Title2", "Author2", 2021, 29.99, false));
        }
    }
}