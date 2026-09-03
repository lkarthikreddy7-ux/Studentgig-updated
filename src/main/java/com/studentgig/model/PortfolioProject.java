package com.studentgig.model;

import java.time.LocalDateTime;

public class PortfolioProject {
    private Long id;
    private Long studentId;
    private String title;
    private String description;
    private java.util.List<String> skills = new java.util.ArrayList<>();
    private String projectLink;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.util.List<String> getSkills() { return skills; }
    public void setSkills(java.util.List<String> skills) { this.skills = skills; }
    public String getProjectLink() { return projectLink; }
    public void setProjectLink(String projectLink) { this.projectLink = projectLink; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
