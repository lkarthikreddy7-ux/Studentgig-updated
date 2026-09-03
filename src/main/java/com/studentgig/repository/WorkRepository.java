package com.studentgig.repository;

import com.studentgig.model.WorkSubmission;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class WorkRepository {
    private final Map<Long, WorkSubmission> submissionsById = new HashMap<>();
    private final Map<Long, WorkSubmission> submissionsByJobId = new HashMap<>();
    private long nextId = 1;

    public WorkSubmission save(WorkSubmission submission) {
        if (submission.getId() == null) {
            submission.setId(nextId++);
        }
        submissionsById.put(submission.getId(), submission);
        submissionsByJobId.put(submission.getJobId(), submission);
        return submission;
    }

    public Optional<WorkSubmission> findById(Long id) {
        return Optional.ofNullable(submissionsById.get(id));
    }

    public Optional<WorkSubmission> findByJobId(Long jobId) {
        return Optional.ofNullable(submissionsByJobId.get(jobId));
    }

    public void setNextId(long id) {
        this.nextId = id;
    }
}
