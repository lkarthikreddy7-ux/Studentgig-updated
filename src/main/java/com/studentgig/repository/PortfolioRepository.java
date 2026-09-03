package com.studentgig.repository;

import com.studentgig.model.PortfolioProject;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PortfolioRepository {
    private final Map<Long, PortfolioProject> projects = new HashMap<>();
    private long nextId = 1;

    public PortfolioProject save(PortfolioProject project) {
        if (project.getId() == null) project.setId(nextId++);
        projects.put(project.getId(), project);
        return project;
    }

    public Optional<PortfolioProject> findById(Long id) { return Optional.ofNullable(projects.get(id)); }

    public List<PortfolioProject> findByStudentId(Long studentId) {
        List<PortfolioProject> result = new ArrayList<>();
        for (PortfolioProject p : projects.values()) if (studentId.equals(p.getStudentId())) result.add(p);
        return result;
    }

    public void delete(Long id) { projects.remove(id); }
}
