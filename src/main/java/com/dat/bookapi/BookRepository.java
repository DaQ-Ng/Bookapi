package com.dat.bookapi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends JpaRepository<Book, Integer>{
    Page<Book> findByTitleContainingIgnoreCaseAndAvailableTrue(String title, Pageable pageable);
}