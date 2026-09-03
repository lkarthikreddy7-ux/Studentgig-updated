package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.Proposal;
import com.studentgig.model.Review;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.ProposalRepository;
import com.studentgig.repository.ReviewRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ProposalRepository proposalRepository;
    private final ReviewRepository reviewRepository;

    public UserService(UserRepository userRepository, JobRepository jobRepository,
                       ProposalRepository proposalRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.proposalRepository = proposalRepository;
        this.reviewRepository = reviewRepository;
    }

    public Map<String, Object> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", 404));
        return toPublicUser(user);
    }

    public Map<String, Object> updateUser(Long id, User updates, Long currentUserId) {
        if (!id.equals(currentUserId)) {
            throw new ApiException("You can only edit your own profile", 403);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", 404));
        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getCollege() != null) user.setCollege(updates.getCollege());
        if (updates.getDepartment() != null) user.setDepartment(updates.getDepartment());
        if (updates.getYear() != null) user.setYear(updates.getYear());
        if (updates.getSkills() != null) user.setSkills(updates.getSkills());
        userRepository.save(user);
        return toPublicUser(user);
    }

    public Map<String, Object> getUserStats(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", 404));

        List<Job> clientJobs = jobRepository.findByClientId(id);
        List<Job> freelancerJobs = jobRepository.findByFreelancerId(id);
        List<Proposal> proposals = proposalRepository.findByFreelancerId(id);

        long jobsPosted = clientJobs.size();
        long jobsCompleted = clientJobs.stream()
                .filter(j -> j.getStatus() == JobStatus.COMPLETED).count();
        long jobsWorkedOn = freelancerJobs.stream()
                .filter(j -> j.getStatus() == JobStatus.COMPLETED).count();
        long applications = proposals.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("jobsPosted", jobsPosted);
        stats.put("jobsCompleted", jobsCompleted);
        stats.put("jobsWorkedOn", jobsWorkedOn);
        stats.put("applications", applications);
        stats.put("averageRating", user.getRating());
        stats.put("totalReviews", user.getTotalReviews());
        return stats;
    }

    public Map<String, Object> getProfileCompletion(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", 404));
        int total = 5;
        int complete = 0;
        if (user.getName() != null && !user.getName().isBlank()) complete++;
        if (user.getCollege() != null && !user.getCollege().isBlank()) complete++;
        if (user.getDepartment() != null && !user.getDepartment().isBlank()) complete++;
        if (user.getYear() != null && !user.getYear().isBlank()) complete++;
        if (user.getSkills() != null && !user.getSkills().isEmpty()) complete++;
        Map<String,Object> result = new HashMap<>();
        result.put("completed", complete);
        result.put("total", total);
        result.put("percentage", complete * 100 / total);
        result.put("missing", List.of(
                user.getCollege() == null || user.getCollege().isBlank() ? "College / Organization" : "",
                user.getDepartment() == null || user.getDepartment().isBlank() ? "Department / Field" : "",
                user.getYear() == null || user.getYear().isBlank() ? "Status" : "",
                user.getSkills() == null || user.getSkills().isEmpty() ? "Skills" : ""
        ).stream().filter(x -> !x.isBlank()).toList());
        return result;
    }

    public Map<String, Object> toPublicUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("college", user.getCollege());
        map.put("department", user.getDepartment());
        map.put("year", user.getYear());
        map.put("skills", user.getSkills());
        map.put("rating", user.getRating());
        map.put("totalReviews", user.getTotalReviews());
        return map;
    }

    public List<Map<String, Object>> getReviewsForUser(Long userId) {
        return reviewRepository.findByReviewedUserId(userId).stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toReviewResponse(Review review) {
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
