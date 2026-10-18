package com.dat.bookapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner{
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public DataSeeder(BookRepository bookRepository, UserRepository userRepository){
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args){
        if(bookRepository.count() == 0){
            User dat = userRepository.save(new User("Dat","d@gmai.com","1035"));
            Book b1 = new Book("Title1", "Author1", 2020, 19.99, true);
            Book b2 = new Book("Title2", "Author2", 2021, 29.99, false);
            b1.setSeller(dat);
            b2.setSeller(dat);
            bookRepository.save(b1);
            bookRepository.save(b2);
        }
    }
}