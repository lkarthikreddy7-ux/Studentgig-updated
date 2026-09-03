package com.studentgig.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Job {
    private Long id;
    private String title;
    private String description;
    private String category;
    private List<String> skillsRequired = new ArrayList<>();
    private double budget;
    private String deadline;
    private Long clientId;
    private Long freelancerId;
    private JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Job() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(List<String> skillsRequired) {
        this.skillsRequired = skillsRequired != null ? skillsRequired : new ArrayList<>();
    }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
