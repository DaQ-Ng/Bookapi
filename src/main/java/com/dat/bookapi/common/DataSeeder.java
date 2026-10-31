package com.dat.bookapi.common;

import com.dat.bookapi.book.Book;
import com.dat.bookapi.book.BookRepository;
import com.dat.bookapi.user.User;
import com.dat.bookapi.user.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataSeeder implements CommandLineRunner{
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(BookRepository bookRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){
        if(bookRepository.count() == 0){
            User dat = userRepository.save(new User("Dat","d@gmai.com", passwordEncoder.encode("1035")));
            Book b1 = new Book("Title1", "Author1", 2020, 19.99, true);
            Book b2 = new Book("Title2", "Author2", 2021, 29.99, false);
            b1.setSeller(dat);
            b2.setSeller(dat);
            bookRepository.save(b1);
            bookRepository.save(b2);
        }
    }
}