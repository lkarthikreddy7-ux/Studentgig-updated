package com.studentgig.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String college;
    private String department;
    private String year;
    private List<String> skills = new ArrayList<>();
    private double rating;
    private int totalReviews;
    private boolean blocked;

    public User() {}

    public User(Long id, String name, String email, String password, String college,
                String department, String year, List<String> skills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.college = college;
        this.department = department;
        this.year = year;
        if (skills != null) {
            this.skills = new ArrayList<>(skills);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills != null ? skills : new ArrayList<>(); }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
