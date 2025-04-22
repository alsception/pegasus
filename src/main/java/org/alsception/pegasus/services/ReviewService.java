package org.alsception.pegasus.services;

import java.util.List;
import org.alsception.pegasus.entities.PGSReview;
import org.alsception.pegasus.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public List<PGSReview> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    
    public PGSReview saveReview(PGSReview review) {
        return reviewRepository.save(review);
    }
}