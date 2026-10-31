package com.dat.bookapi;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import java.util.List;

@RestController
public class ReviewController{
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public Review postReview(@Valid @RequestBody Review review){
        return reviewService.addReview(review);
    }

    @GetMapping("/reviews")
    public List<Review> getReviews(){
        return reviewService.getAllReview();
    }

    
}