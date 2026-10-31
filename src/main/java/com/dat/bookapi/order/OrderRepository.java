package com.dat.bookapi.order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer>{
    //Yes, it’s empty. All the methods come from JpaRepository.
}