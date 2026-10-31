package com.dat.bookapi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "orders")
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book purchasedBook;
    @NotNull
    @PositiveOrZero
    private Double price;
    private LocalDateTime createdAt;

    public Order(){}

    Order(User buyer, Book purchasedBook, Double price){
        setBuyer(buyer);
        setBook(purchasedBook);
        setPrice(price);
        this.createdAt = LocalDateTime.now();
    }

    public void setBuyer(User buyer){
        this.buyer = buyer;
    }

    public void setBook(Book purchasedBook){
        this.purchasedBook = purchasedBook;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public User getBuyer(){
        return buyer;
    }

    public Book getPurchasedBook(){
        return purchasedBook;
    }

    public Double getPrice(){
        return price;
    }

    public Integer getId(){
        return id;
    }

    public LocalDateTime getDate(){
        return createdAt;
    }
}

