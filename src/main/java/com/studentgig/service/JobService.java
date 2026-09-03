package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public JobService(JobRepository jobRepository, UserRepository userRepository,
                      NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<Map<String, Object>> getJobs(String search, String category, JobStatus status) {
        return getJobs(search, category, status, "newest");
    }

    public List<Map<String, Object>> getJobs(String search, String category, JobStatus status, String sort) {
        return jobRepository.findAll().stream()
                .filter(j -> status == null || j.getStatus() == status)
                .filter(j -> category == null || category.isBlank() ||
                        j.getCategory().equalsIgnoreCase(category))
                .filter(j -> search == null || search.isBlank() || matchesSearch(j, search))
                .sorted((a, b) -> compareJobs(a, b, sort))
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }


    private int compareJobs(Job a, Job b, String sort) {
        if ("budget-high".equalsIgnoreCase(sort)) return Double.compare(b.getBudget(), a.getBudget());
        if ("budget-low".equalsIgnoreCase(sort)) return Double.compare(a.getBudget(), b.getBudget());
        if ("deadline".equalsIgnoreCase(sort)) return String.valueOf(a.getDeadline()).compareTo(String.valueOf(b.getDeadline()));
        return b.getCreatedAt().compareTo(a.getCreatedAt());
    }

    private boolean matchesSearch(Job job, String search) {
        String s = search.toLowerCase();
        return job.getTitle().toLowerCase().contains(s)
                || job.getDescription().toLowerCase().contains(s)
                || job.getCategory().toLowerCase().contains(s)
                || job.getSkillsRequired().stream().anyMatch(sk -> sk.toLowerCase().contains(s));
    }

    public Map<String, Object> getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException("Job not found", 404));
        return toJobResponse(job);
    }

    public Map<String, Object> createJob(Job job, Long clientId) {
        job.setClientId(clientId);
        job.setStatus(JobStatus.OPEN);
        job.setCreatedAt(LocalDateTime.now());
        job.setFreelancerId(null);
        Job saved = jobRepository.save(job);
        return toJobResponse(saved);
    }

    public Map<String, Object> updateJob(Long id, Job updates, Long currentUserId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException("Job not found", 404));
        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("You can only edit your own jobs", 403);
        }
        if (job.getStatus() != JobStatus.OPEN) {
            throw new ApiException("Only OPEN jobs can be edited", 400);
        }
        if (updates.getTitle() != null) job.setTitle(updates.getTitle());
        if (updates.getDescription() != null) job.setDescription(updates.getDescription());
        if (updates.getCategory() != null) job.setCategory(updates.getCategory());
        if (updates.getSkillsRequired() != null) job.setSkillsRequired(updates.getSkillsRequired());
        if (updates.getBudget() > 0) job.setBudget(updates.getBudget());
        if (updates.getDeadline() != null) job.setDeadline(updates.getDeadline());
        jobRepository.save(job);
        return toJobResponse(job);
    }

    public Map<String, Object> cancelJob(Long id, Long currentUserId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException("Job not found", 404));
        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("You can only cancel your own jobs", 403);
        }
        if (job.getStatus() == JobStatus.COMPLETED) {
            throw new ApiException("Completed jobs cannot be cancelled", 400);
        }
        job.setStatus(JobStatus.CANCELLED);
        jobRepository.save(job);
        if (job.getFreelancerId() != null) {
            notificationService.notify(job.getFreelancerId(),
                    "Job \"" + job.getTitle() + "\" has been cancelled by the client.");
        }
        return toJobResponse(job);
    }

    public List<Map<String, Object>> getActiveJobs(Long userId) {
        return jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.IN_PROGRESS || j.getStatus() == JobStatus.SUBMITTED)
                .filter(j -> j.getClientId().equals(userId) ||
                        (j.getFreelancerId() != null && j.getFreelancerId().equals(userId)))
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getCompletedJobs(Long userId) {
        return jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.COMPLETED)
                .filter(j -> j.getClientId().equals(userId) ||
                        (j.getFreelancerId() != null && j.getFreelancerId().equals(userId)))
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMyPostedJobs(Long userId) {
        return jobRepository.findByClientId(userId).stream()
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> toJobResponse(Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", job.getId());
        map.put("title", job.getTitle());
        map.put("description", job.getDescription());
        map.put("category", job.getCategory());
        map.put("skillsRequired", job.getSkillsRequired());
        map.put("budget", job.getBudget());
        map.put("deadline", job.getDeadline());
        map.put("clientId", job.getClientId());
        map.put("freelancerId", job.getFreelancerId());
        map.put("status", job.getStatus().name());
        map.put("createdAt", job.getCreatedAt());
        map.put("completedAt", job.getCompletedAt());

        userRepository.findById(job.getClientId()).ifPresent(client -> {
            map.put("clientName", client.getName());
            map.put("clientRating", client.getRating());
        });
        if (job.getFreelancerId() != null) {
            userRepository.findById(job.getFreelancerId()).ifPresent(f -> {
                map.put("freelancerName", f.getName());
                map.put("freelancerRating", f.getRating());
            });
        }
        return map;
    }
}
