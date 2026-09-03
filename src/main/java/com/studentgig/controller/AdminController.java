package com.studentgig.controller;

import com.studentgig.service.AdminService;
import com.studentgig.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService; private final AuthService authService;
    public AdminController(AdminService adminService, AuthService authService){this.adminService=adminService;this.authService=authService;}
    private void check(HttpSession s){adminService.requireAdmin(authService.requireUser(s));}
    @GetMapping("/stats") public Map<String,Object> stats(HttpSession s){check(s);return adminService.stats();}
    @GetMapping("/users") public List<Map<String,Object>> users(HttpSession s){check(s);return adminService.users();}
    @PutMapping("/users/{id}/block") public ResponseEntity<Map<String,String>> block(@PathVariable Long id,HttpSession s){check(s);adminService.setBlocked(id,true);return ResponseEntity.ok(Map.of("message","User blocked"));}
    @PutMapping("/users/{id}/unblock") public ResponseEntity<Map<String,String>> unblock(@PathVariable Long id,HttpSession s){check(s);adminService.setBlocked(id,false);return ResponseEntity.ok(Map.of("message","User unblocked"));}
    @GetMapping("/jobs") public List<Map<String,Object>> jobs(HttpSession s){check(s);return adminService.jobs();}
    @PutMapping("/jobs/{id}/cancel") public ResponseEntity<Map<String,String>> cancel(@PathVariable Long id,HttpSession s){check(s);adminService.cancelJob(id);return ResponseEntity.ok(Map.of("message","Job cancelled"));}
}
