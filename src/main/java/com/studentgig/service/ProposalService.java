package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.Proposal;
import com.studentgig.model.ProposalStatus;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.ProposalRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProposalService(ProposalRepository proposalRepository, JobRepository jobRepository,
                           UserRepository userRepository, NotificationService notificationService) {
        this.proposalRepository = proposalRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Map<String, Object> submitProposal(Long jobId, Proposal proposal, User freelancer) {
        if (freelancer.isBlocked()) {
            throw new ApiException("Your account is blocked and cannot apply", 403);
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));
        if (job.getStatus() != JobStatus.OPEN) {
            throw new ApiException("This job is not accepting proposals", 400);
        }
        if (job.getClientId().equals(freelancer.getId())) {
            throw new ApiException("You cannot apply to your own job", 400);
        }
        if (proposalRepository.findByJobIdAndFreelancerId(jobId, freelancer.getId()).isPresent()) {
            throw new ApiException("You have already applied to this job", 400);
        }

        proposal.setJobId(jobId);
        proposal.setFreelancerId(freelancer.getId());
        proposal.setStatus(ProposalStatus.PENDING);
        proposal.setCreatedAt(LocalDateTime.now());
        Proposal saved = proposalRepository.save(proposal);

        notificationService.notify(job.getClientId(),
                freelancer.getName() + " submitted a proposal for \"" + job.getTitle() + "\".");

        return toProposalResponse(saved);
    }

    public List<Map<String, Object>> getProposalsForJob(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));
        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("Only the job client can view proposals", 403);
        }
        return proposalRepository.findByJobId(jobId).stream()
                .map(this::toProposalResponse)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMyProposals(Long freelancerId) {
        return proposalRepository.findByFreelancerId(freelancerId).stream()
                .map(p -> {
                    Map<String, Object> map = toProposalResponse(p);
                    jobRepository.findById(p.getJobId()).ifPresent(j -> {
                        map.put("jobTitle", j.getTitle());
                        map.put("jobStatus", j.getStatus().name());
                    });
                    return map;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> acceptProposal(Long proposalId, Long currentUserId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Proposal not found", 404));
        Job job = jobRepository.findById(proposal.getJobId())
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("Only the job client can hire freelancers", 403);
        }
        if (job.getStatus() != JobStatus.OPEN) {
            throw new ApiException("Job is no longer open for hiring", 400);
        }
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new ApiException("Proposal is not pending", 400);
        }

        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposalRepository.save(proposal);

        proposalRepository.findByJobId(job.getId()).stream()
                .filter(p -> !p.getId().equals(proposalId) && p.getStatus() == ProposalStatus.PENDING)
                .forEach(p -> {
                    p.setStatus(ProposalStatus.REJECTED);
                    proposalRepository.save(p);
                    notificationService.notify(p.getFreelancerId(),
                            "Your proposal for \"" + job.getTitle() + "\" was not selected.");
                });

        job.setFreelancerId(proposal.getFreelancerId());
        job.setStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        User freelancer = userRepository.findById(proposal.getFreelancerId()).orElse(null);
        String freelancerName = freelancer != null ? freelancer.getName() : "A freelancer";
        notificationService.notify(proposal.getFreelancerId(),
                "Congratulations! You were hired for \"" + job.getTitle() + "\".");

        Map<String, Object> result = toProposalResponse(proposal);
        result.put("message", freelancerName + " has been hired successfully.");
        return result;
    }

    public Map<String, Object> rejectProposal(Long proposalId, Long currentUserId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Proposal not found", 404));
        Job job = jobRepository.findById(proposal.getJobId())
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("Only the job client can reject proposals", 403);
        }
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new ApiException("Proposal is not pending", 400);
        }

        proposal.setStatus(ProposalStatus.REJECTED);
        proposalRepository.save(proposal);

        notificationService.notify(proposal.getFreelancerId(),
                "Your proposal for \"" + job.getTitle() + "\" was rejected.");

        return toProposalResponse(proposal);
    }

    public Map<String, Object> toProposalResponse(Proposal proposal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", proposal.getId());
        map.put("jobId", proposal.getJobId());
        map.put("freelancerId", proposal.getFreelancerId());
        map.put("proposedPrice", proposal.getProposedPrice());
        map.put("deliveryDays", proposal.getDeliveryDays());
        map.put("coverMessage", proposal.getCoverMessage());
        map.put("status", proposal.getStatus().name());
        map.put("createdAt", proposal.getCreatedAt());

        userRepository.findById(proposal.getFreelancerId()).ifPresent(f -> {
            map.put("freelancerName", f.getName());
            map.put("freelancerSkills", f.getSkills());
            map.put("freelancerRating", f.getRating());
            map.put("freelancerReviews", f.getTotalReviews());
        });
        return map;
    }
}
