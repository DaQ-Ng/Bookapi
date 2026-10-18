package com.dat.bookapi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    //Yes, it’s empty. All the methods come from JpaRepository.
}