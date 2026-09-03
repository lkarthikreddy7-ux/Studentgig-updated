package com.studentgig.repository;

import com.studentgig.model.Proposal;
import com.studentgig.model.ProposalStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProposalRepository {
    private final Map<Long, Proposal> proposalsById = new HashMap<>();
    private long nextId = 1;

    public Proposal save(Proposal proposal) {
        if (proposal.getId() == null) {
            proposal.setId(nextId++);
        }
        proposalsById.put(proposal.getId(), proposal);
        return proposal;
    }

    public Optional<Proposal> findById(Long id) {
        return Optional.ofNullable(proposalsById.get(id));
    }

    public List<Proposal> findByJobId(Long jobId) {
        return proposalsById.values().stream()
                .filter(p -> p.getJobId().equals(jobId))
                .collect(Collectors.toList());
    }

    public List<Proposal> findByFreelancerId(Long freelancerId) {
        return proposalsById.values().stream()
                .filter(p -> p.getFreelancerId().equals(freelancerId))
                .collect(Collectors.toList());
    }

    public Optional<Proposal> findByJobIdAndFreelancerId(Long jobId, Long freelancerId) {
        return proposalsById.values().stream()
                .filter(p -> p.getJobId().equals(jobId) && p.getFreelancerId().equals(freelancerId))
                .findFirst();
    }

    public List<Proposal> findByJobIdAndStatus(Long jobId, ProposalStatus status) {
        return proposalsById.values().stream()
                .filter(p -> p.getJobId().equals(jobId) && p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Proposal> findAll() {
        return new ArrayList<>(proposalsById.values());
    }

    public void setNextId(long id) {
        this.nextId = id;
    }
}
