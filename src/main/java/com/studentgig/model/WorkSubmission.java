package com.studentgig.model;

import java.time.LocalDateTime;

public class WorkSubmission {
    private Long id;
    private Long jobId;
    private Long freelancerId;
    private String workLink;
    private String message;
    private WorkStatus status;
    private LocalDateTime submittedAt;
    private String changeRequestMessage;

    public WorkSubmission() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }

    public String getWorkLink() { return workLink; }
    public void setWorkLink(String workLink) { this.workLink = workLink; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public WorkStatus getStatus() { return status; }
    public void setStatus(WorkStatus status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getChangeRequestMessage() { return changeRequestMessage; }
    public void setChangeRequestMessage(String changeRequestMessage) { this.changeRequestMessage = changeRequestMessage; }
}
