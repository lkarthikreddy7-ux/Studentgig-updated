package com.studentgig.repository;

import com.studentgig.model.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReviewRepository {
    private final Map<Long, Review> reviewsById = new HashMap<>();
    private long nextId = 1;

    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(nextId++);
        }
        reviewsById.put(review.getId(), review);
        return review;
    }

    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(reviewsById.get(id));
    }

    public List<Review> findByReviewedUserId(Long userId) {
        return reviewsById.values().stream()
                .filter(r -> r.getReviewedUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Review> findByJobId(Long jobId) {
        return reviewsById.values().stream()
                .filter(r -> r.getJobId().equals(jobId))
                .findFirst();
    }

    public List<Review> findAll() {
        return new ArrayList<>(reviewsById.values());
    }

    public void setNextId(long id) {
        this.nextId = id;
    }
}
