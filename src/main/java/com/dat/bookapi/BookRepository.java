package com.dat.bookapi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer>{
    //Yes, it’s empty. All the methods come from JpaRepository.
}