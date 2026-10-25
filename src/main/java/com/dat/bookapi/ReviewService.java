package com.dat.bookapi;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService{
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReview(){
        return reviewRepository.findAll();
    }

    public Review addReview(Review review){
        review.setId(null);
        return reviewRepository.save(review);
    }

    public Review deleteReview(int id){
        Review reviewToDelete = reviewRepository.findById(id).orElse(null);
        if(reviewToDelete != null){
            reviewRepository.deleteById(id);
            return reviewToDelete;
        }
        return null;
    }

    public Review updateReview(int id, Review updateReview){
        if(reviewRepository.existsById(id)){
            updateReview.setId(id);
            return reviewRepository.save(updateReview);
        }
        return null;
    }
}