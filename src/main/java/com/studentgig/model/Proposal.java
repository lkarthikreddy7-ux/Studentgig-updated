package com.studentgig.model;

import java.time.LocalDateTime;

public class Proposal {
    private Long id;
    private Long jobId;
    private Long freelancerId;
    private double proposedPrice;
    private int deliveryDays;
    private String coverMessage;
    private ProposalStatus status;
    private LocalDateTime createdAt;

    public Proposal() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }

    public double getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(double proposedPrice) { this.proposedPrice = proposedPrice; }

    public int getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(int deliveryDays) { this.deliveryDays = deliveryDays; }

    public String getCoverMessage() { return coverMessage; }
    public void setCoverMessage(String coverMessage) { this.coverMessage = coverMessage; }

    public ProposalStatus getStatus() { return status; }
    public void setStatus(ProposalStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
