package com.studentgig.controller;

import com.studentgig.model.Review;
import com.studentgig.service.AuthService;
import com.studentgig.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final AuthService authService;

    public ReviewController(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @PostMapping("/api/jobs/{jobId}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(@PathVariable Long jobId,
                                                            @RequestBody Review review,
                                                            HttpSession session) {
        var user = authService.requireUser(session);
        Map<String, Object> result = reviewService.createReview(jobId, review, user);
        result.put("message", "Review submitted successfully.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/users/{userId}/reviews")
    public ResponseEntity<List<Map<String, Object>>> getReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }
}
