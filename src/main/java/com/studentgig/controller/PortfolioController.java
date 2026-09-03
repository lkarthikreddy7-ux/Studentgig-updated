package com.studentgig.controller;

import com.studentgig.model.PortfolioProject;
import com.studentgig.service.AuthService;
import com.studentgig.service.PortfolioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final AuthService authService;
    public PortfolioController(PortfolioService portfolioService, AuthService authService) {
        this.portfolioService = portfolioService; this.authService = authService;
    }
    @GetMapping
    public ResponseEntity<List<PortfolioProject>> list(HttpSession session) {
        return ResponseEntity.ok(portfolioService.list(authService.requireUser(session).getId()));
    }
    @PostMapping
    public ResponseEntity<PortfolioProject> create(@RequestBody PortfolioProject project, HttpSession session) {
        return ResponseEntity.ok(portfolioService.create(project, authService.requireUser(session).getId()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PortfolioProject> update(@PathVariable Long id, @RequestBody PortfolioProject project, HttpSession session) {
        return ResponseEntity.ok(portfolioService.update(id, project, authService.requireUser(session).getId()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        portfolioService.delete(id, authService.requireUser(session).getId());
        return ResponseEntity.noContent().build();
    }
}
