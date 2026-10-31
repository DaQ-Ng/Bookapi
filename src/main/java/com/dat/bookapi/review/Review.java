package com.dat.bookapi.review;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Review{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @PositiveOrZero
    private Integer rating;
    @NotBlank
    private String comment;

    public Review(){}

    public Review(Integer rating, String comment){
        setRating(rating);
        setComment(comment);
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setRating(Integer rating){
        this.rating = rating;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public Integer getId(){
        return id;
    }

    public Integer getRating(){
        return rating;
    }

    public String getComment(){
        return comment;
    }
}