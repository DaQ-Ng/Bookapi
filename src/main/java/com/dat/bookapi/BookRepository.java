package com.dat.bookapi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer>{
    //Yes, it’s empty. All the methods come from JpaRepository.
    
    List<Book> findByTitleContainingIgnoreCaseAndAvailableTrue(String title);
}