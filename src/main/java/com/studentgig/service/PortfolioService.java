package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.PortfolioProject;
import com.studentgig.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PortfolioService {
    private final PortfolioRepository repository;
    private final AuthService authService;

    public PortfolioService(PortfolioRepository repository, AuthService authService) {
        this.repository = repository; this.authService = authService;
    }

    public List<PortfolioProject> list(Long studentId) {
        return repository.findByStudentId(studentId).stream()
                .sorted(Comparator.comparing(PortfolioProject::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public PortfolioProject create(PortfolioProject project, Long studentId) {
        validate(project);
        project.setId(null); project.setStudentId(studentId); project.setCreatedAt(LocalDateTime.now());
        return repository.save(project);
    }

    public PortfolioProject update(Long id, PortfolioProject updates, Long studentId) {
        PortfolioProject p = repository.findById(id).orElseThrow(() -> new ApiException("Portfolio project not found", 404));
        if (!studentId.equals(p.getStudentId())) throw new ApiException("You can only edit your own portfolio", 403);
        if (updates.getTitle() != null && !updates.getTitle().isBlank()) p.setTitle(updates.getTitle());
        if (updates.getDescription() != null) p.setDescription(updates.getDescription());
        if (updates.getSkills() != null) p.setSkills(updates.getSkills());
        if (updates.getProjectLink() != null) p.setProjectLink(updates.getProjectLink());
        return repository.save(p);
    }

    public void delete(Long id, Long studentId) {
        PortfolioProject p = repository.findById(id).orElseThrow(() -> new ApiException("Portfolio project not found", 404));
        if (!studentId.equals(p.getStudentId())) throw new ApiException("You can only delete your own portfolio", 403);
        repository.delete(id);
    }

    private void validate(PortfolioProject p) {
        if (p.getTitle() == null || p.getTitle().isBlank()) throw new ApiException("Project title is required", 400);
        if (p.getDescription() == null || p.getDescription().isBlank()) throw new ApiException("Project description is required", 400);
    }
}
