package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    public static final String ADMIN_EMAIL = "admin@studentgig.com";
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    public AdminService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository; this.jobRepository = jobRepository;
    }
    public void requireAdmin(User user) {
        if (user == null || !ADMIN_EMAIL.equalsIgnoreCase(user.getEmail())) throw new ApiException("Admin access required", 403);
    }
    public List<Map<String,Object>> users() {
        return userRepository.findAll().stream().map(u -> {
            Map<String,Object> m = new HashMap<>(); m.put("id",u.getId()); m.put("name",u.getName()); m.put("email",u.getEmail());
            m.put("college",u.getCollege()); m.put("blocked",u.isBlocked()); m.put("rating",u.getRating()); m.put("totalReviews",u.getTotalReviews()); return m;
        }).toList();
    }
    public void setBlocked(Long id, boolean blocked) {
        User u = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found",404));
        if (ADMIN_EMAIL.equalsIgnoreCase(u.getEmail())) throw new ApiException("Admin account cannot be blocked",400);
        u.setBlocked(blocked); userRepository.save(u);
    }
    public List<Map<String,Object>> jobs() {
        return jobRepository.findAll().stream().map(j -> {
            Map<String,Object> m = new HashMap<>(); m.put("id",j.getId()); m.put("title",j.getTitle()); m.put("category",j.getCategory());
            m.put("budget",j.getBudget()); m.put("status",j.getStatus().name()); m.put("clientId",j.getClientId()); return m;
        }).toList();
    }
    public void cancelJob(Long id) {
        Job j = jobRepository.findById(id).orElseThrow(() -> new ApiException("Job not found",404));
        if (j.getStatus() == JobStatus.COMPLETED) throw new ApiException("Completed jobs cannot be cancelled",400);
        j.setStatus(JobStatus.CANCELLED); jobRepository.save(j);
    }
    public Map<String,Object> stats() {
        List<User> users=userRepository.findAll(); List<Job> jobs=jobRepository.findAll();
        Map<String,Object> m=new LinkedHashMap<>(); m.put("totalUsers",users.size()); m.put("blockedUsers",users.stream().filter(User::isBlocked).count());
        m.put("totalJobs",jobs.size()); m.put("openJobs",jobs.stream().filter(j->j.getStatus()==JobStatus.OPEN).count());
        m.put("activeJobs",jobs.stream().filter(j->j.getStatus()==JobStatus.IN_PROGRESS || j.getStatus()==JobStatus.SUBMITTED).count());
        m.put("completedJobs",jobs.stream().filter(j->j.getStatus()==JobStatus.COMPLETED).count()); return m;
    }
}
