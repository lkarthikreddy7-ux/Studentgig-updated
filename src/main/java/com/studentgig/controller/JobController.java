package com.studentgig.controller;

import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.service.AuthService;
import com.studentgig.service.JobService;
import com.studentgig.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;
    private final AuthService authService;
    private final RecommendationService recommendationService;

    public JobController(JobService jobService, AuthService authService, RecommendationService recommendationService) {
        this.jobService = jobService;
        this.authService = authService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "newest") String sort) {
        JobStatus jobStatus = null;
        if (status != null && !status.isBlank()) {
            jobStatus = JobStatus.valueOf(status.toUpperCase());
        } else {
            jobStatus = JobStatus.OPEN;
        }
        return ResponseEntity.ok(jobService.getJobs(search, category, jobStatus, sort));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "newest") String sort) {
        JobStatus jobStatus = null;
        if (status != null && !status.isBlank()) {
            jobStatus = JobStatus.valueOf(status.toUpperCase());
        }
        return ResponseEntity.ok(jobService.getJobs(search, category, jobStatus, sort));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<Map<String, Object>>> getRecommendedJobs(HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(recommendationService.getRecommendedJobs(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(@RequestBody Job job, HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        Map<String, Object> created = jobService.createJob(job, userId);
        created.put("message", "Job posted successfully.");
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(@PathVariable Long id,
                                                         @RequestBody Job job,
                                                         HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(jobService.updateJob(id, job, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelJob(@PathVariable Long id, HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(jobService.cancelJob(id, userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveJobs(HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(jobService.getActiveJobs(userId));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Map<String, Object>>> getCompletedJobs(HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(jobService.getCompletedJobs(userId));
    }

    @GetMapping("/my-posted")
    public ResponseEntity<List<Map<String, Object>>> getMyPostedJobs(HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(jobService.getMyPostedJobs(userId));
    }
}
