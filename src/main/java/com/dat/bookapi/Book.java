package com.dat.bookapi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Book{
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotNull
    @Min(0)
    @Max(2026)
    private Integer year;
    @NotNull
    @PositiveOrZero
    private Double price;
    @NotNull
    private Boolean available;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Book() {
    }

    Book(String title, String author, int year, double price, boolean available){
        setTitle(title);
        setAuthor(author);
        setYear(year);
        setPrice(price);
        setAvailable(available);
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getYear() {
        return year;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setYear(Integer year){
        this.year = year;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    public void setAvailable(Boolean available){
        this.available = available;
    }
}