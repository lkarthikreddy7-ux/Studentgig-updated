package com.studentgig.controller;

import com.studentgig.model.User;
import com.studentgig.service.AuthService;
import com.studentgig.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                         @RequestBody User updates,
                                                         HttpSession session) {
        Long currentUserId = authService.requireUser(session).getId();
        return ResponseEntity.ok(userService.updateUser(id, updates, currentUserId));
    }

    @GetMapping("/{id}/profile-completion")
    public ResponseEntity<Map<String, Object>> getProfileCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfileCompletion(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserStats(id));
    }

}
