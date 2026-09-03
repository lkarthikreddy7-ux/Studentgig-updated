package com.studentgig.controller;

import com.studentgig.model.User;
import com.studentgig.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user, HttpSession session) {
        return ResponseEntity.ok(authService.register(user, session));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        return ResponseEntity.ok(authService.login(body.get("email"), body.get("password"), session));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Map<String, Object> user = authService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }
        user.put("loggedIn", true);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/top-freelancers")
    public ResponseEntity<List<Map<String, Object>>> getTopFreelancers() {
        return ResponseEntity.ok(authService.getTopFreelancers(5).stream()
                .map(authService::toUserResponse)
                .toList());
    }
}
