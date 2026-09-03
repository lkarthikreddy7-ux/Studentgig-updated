package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.User;
import com.studentgig.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    public static final String SESSION_USER_ID = "userId";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> register(User user, HttpSession session) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ApiException("Email is required", 400);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ApiException("Password is required", 400);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ApiException("Name is required", 400);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApiException("Email already registered", 400);
        }
        user.setRating(0);
        user.setTotalReviews(0);
        user.setBlocked(false);
        User saved = userRepository.save(user);
        session.setAttribute(SESSION_USER_ID, saved.getId());
        return toUserResponse(saved);
    }

    public Map<String, Object> login(String email, String password, HttpSession session) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Invalid email or password", 401));
        if (!user.getPassword().equals(password)) {
            throw new ApiException("Invalid email or password", 401);
        }
        if (user.isBlocked()) {
            throw new ApiException("Your account has been blocked", 403);
        }
        session.setAttribute(SESSION_USER_ID, user.getId());
        return toUserResponse(user);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public Map<String, Object> getCurrentUser(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            return null;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));
        return toUserResponse(user);
    }

    public Long getCurrentUserId(HttpSession session) {
        if (session == null) return null;
        Object attr = session.getAttribute(SESSION_USER_ID);
        return attr != null ? (Long) attr : null;
    }

    public User requireUser(HttpSession session) {
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            throw new ApiException("You must be logged in", 401);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));
    }

    public Map<String, Object> toUserResponse(User user) {
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

    public List<User> getTopFreelancers(int limit) {
        return userRepository.findAll().stream()
                .filter(u -> u.getTotalReviews() > 0)
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit)
                .toList();
    }
}
