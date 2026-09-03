package com.studentgig.repository;

import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JobRepository {
    private final Map<Long, Job> jobsById = new HashMap<>();
    private long nextId = 1;

    public Job save(Job job) {
        if (job.getId() == null) {
            job.setId(nextId++);
        }
        jobsById.put(job.getId(), job);
        return job;
    }

    public Optional<Job> findById(Long id) {
        return Optional.ofNullable(jobsById.get(id));
    }

    public List<Job> findAll() {
        return new ArrayList<>(jobsById.values());
    }

    public List<Job> findByStatus(JobStatus status) {
        return jobsById.values().stream()
                .filter(j -> j.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Job> findByClientId(Long clientId) {
        return jobsById.values().stream()
                .filter(j -> j.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    public List<Job> findByFreelancerId(Long freelancerId) {
        return jobsById.values().stream()
                .filter(j -> j.getFreelancerId() != null && j.getFreelancerId().equals(freelancerId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        jobsById.remove(id);
    }

    public void setNextId(long id) {
        this.nextId = id;
    }
}
