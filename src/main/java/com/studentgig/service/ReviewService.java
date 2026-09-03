package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.Review;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.ReviewRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ReviewService(ReviewRepository reviewRepository, JobRepository jobRepository,
                         UserRepository userRepository, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Map<String, Object> createReview(Long jobId, Review review, User reviewer) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new ApiException("Reviews can only be given for completed jobs", 400);
        }
        if (!job.getClientId().equals(reviewer.getId())) {
            throw new ApiException("Only the job client can leave a review", 403);
        }
        if (reviewRepository.findByJobId(jobId).isPresent()) {
            throw new ApiException("A review already exists for this job", 400);
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ApiException("Rating must be between 1 and 5", 400);
        }

        review.setJobId(jobId);
        review.setReviewerId(reviewer.getId());
        review.setReviewedUserId(job.getFreelancerId());
        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepository.save(review);

        updateUserRating(job.getFreelancerId());

        notificationService.notify(job.getFreelancerId(),
                "You received a " + review.getRating() + "-star review for \"" + job.getTitle() + "\".");

        return toReviewResponse(saved);
    }

    private void updateUserRating(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserId(userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        user.setRating(Math.round(avg * 10.0) / 10.0);
        user.setTotalReviews(reviews.size());
        userRepository.save(user);
    }

    public List<Map<String, Object>> getReviewsForUser(Long userId) {
        return reviewRepository.findByReviewedUserId(userId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> toReviewResponse(Review review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("jobId", review.getJobId());
        map.put("reviewerId", review.getReviewerId());
        map.put("reviewedUserId", review.getReviewedUserId());
        map.put("rating", review.getRating());
        map.put("comment", review.getComment());
        map.put("createdAt", review.getCreatedAt());

        userRepository.findById(review.getReviewerId()).ifPresent(u -> map.put("reviewerName", u.getName()));
        jobRepository.findById(review.getJobId()).ifPresent(j -> map.put("jobTitle", j.getTitle()));
        return map;
    }
}
