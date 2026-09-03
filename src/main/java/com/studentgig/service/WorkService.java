package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.User;
import com.studentgig.model.WorkSubmission;
import com.studentgig.model.WorkStatus;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.WorkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkService {
    private final WorkRepository workRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;

    public WorkService(WorkRepository workRepository, JobRepository jobRepository,
                       NotificationService notificationService, PaymentService paymentService) {
        this.workRepository = workRepository;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
    }

    public Map<String, Object> submitWork(Long jobId, WorkSubmission submission, User freelancer) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (job.getFreelancerId() == null || !job.getFreelancerId().equals(freelancer.getId())) {
            throw new ApiException("Only the assigned freelancer can submit work", 403);
        }
        if (job.getStatus() != JobStatus.IN_PROGRESS && job.getStatus() != JobStatus.SUBMITTED) {
            throw new ApiException("Work can only be submitted for in-progress jobs", 400);
        }

        WorkSubmission existing = workRepository.findByJobId(jobId).orElse(null);
        if (existing != null) {
            existing.setWorkLink(submission.getWorkLink());
            existing.setMessage(submission.getMessage());
            existing.setStatus(WorkStatus.SUBMITTED);
            existing.setSubmittedAt(LocalDateTime.now());
            workRepository.save(existing);
            submission = existing;
        } else {
            submission.setJobId(jobId);
            submission.setFreelancerId(freelancer.getId());
            submission.setStatus(WorkStatus.SUBMITTED);
            submission.setSubmittedAt(LocalDateTime.now());
            workRepository.save(submission);
        }

        job.setStatus(JobStatus.SUBMITTED);
        jobRepository.save(job);

        notificationService.notify(job.getClientId(),
                "Work has been submitted for \"" + job.getTitle() + "\". Please review and pay if satisfied.");

        return toSubmissionResponse(submission, job);
    }

    public Map<String, Object> getSubmission(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(currentUserId) &&
                (job.getFreelancerId() == null || !job.getFreelancerId().equals(currentUserId))) {
            throw new ApiException("You do not have access to this submission", 403);
        }

        WorkSubmission submission = workRepository.findByJobId(jobId)
                .orElseThrow(() -> new ApiException("No submission found for this job", 404));
        return toSubmissionResponse(submission, job);
    }

    public Map<String, Object> acceptWork(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("Only the job client can accept work", 403);
        }
        if (job.getStatus() != JobStatus.SUBMITTED) {
            throw new ApiException("Job work has not been submitted yet", 400);
        }

        WorkSubmission submission = workRepository.findByJobId(jobId)
                .orElseThrow(() -> new ApiException("No submission found", 404));

        submission.setStatus(WorkStatus.ACCEPTED);
        workRepository.save(submission);

        Map<String, Object> payment = paymentService.processPayment(jobId, currentUserId);

        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        jobRepository.save(job);

        notificationService.notify(job.getFreelancerId(),
                "Your work for \"" + job.getTitle() + "\" has been accepted! Payment is on the way.");

        Map<String, Object> result = toSubmissionResponse(submission, job);
        result.put("payment", payment);
        result.put("message", "Work accepted and payment of ₹" + (int) job.getBudget() + " completed!");
        return result;
    }

    public Map<String, Object> requestChanges(Long jobId, Long currentUserId, String feedback) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(currentUserId)) {
            throw new ApiException("Only the job client can request changes", 403);
        }
        if (job.getStatus() != JobStatus.SUBMITTED) {
            throw new ApiException("Job work has not been submitted yet", 400);
        }
        if (feedback == null || feedback.isBlank()) {
            throw new ApiException("Please describe what changes are needed", 400);
        }

        WorkSubmission submission = workRepository.findByJobId(jobId)
                .orElseThrow(() -> new ApiException("No submission found", 404));

        submission.setStatus(WorkStatus.CHANGES_REQUESTED);
        submission.setChangeRequestMessage(feedback.trim());
        workRepository.save(submission);

        job.setStatus(JobStatus.IN_PROGRESS);
        jobRepository.save(job);

        notificationService.notify(job.getFreelancerId(),
                "Changes requested for \"" + job.getTitle() + "\": " + feedback.trim());

        Map<String, Object> result = toSubmissionResponse(submission, job);
        result.put("message", "Changes requested. Freelancer has been notified with your feedback.");
        return result;
    }

    private Map<String, Object> toSubmissionResponse(WorkSubmission submission, Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", submission.getId());
        map.put("jobId", submission.getJobId());
        map.put("freelancerId", submission.getFreelancerId());
        map.put("workLink", submission.getWorkLink());
        map.put("message", submission.getMessage());
        map.put("status", submission.getStatus().name());
        map.put("submittedAt", submission.getSubmittedAt());
        map.put("changeRequestMessage", submission.getChangeRequestMessage());
        map.put("budget", job.getBudget());
        map.put("jobTitle", job.getTitle());
        return map;
    }
}
