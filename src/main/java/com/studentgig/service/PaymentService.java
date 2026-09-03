package com.studentgig.service;

import com.studentgig.exception.ApiException;
import com.studentgig.model.Job;
import com.studentgig.model.Payment;
import com.studentgig.model.PaymentStatus;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository, JobRepository jobRepository,
                          NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
    }

    public Map<String, Object> processPayment(Long jobId, Long clientId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(clientId)) {
            throw new ApiException("Only the job client can make payment", 403);
        }

        Payment payment = paymentRepository.findByJobId(jobId).orElse(null);
        if (payment == null) {
            payment = new Payment();
            payment.setJobId(jobId);
            payment.setClientId(job.getClientId());
            payment.setFreelancerId(job.getFreelancerId());
            payment.setAmount(job.getBudget());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
        } else if (payment.getStatus() != PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        notificationService.notify(job.getFreelancerId(),
                "Payment of ₹" + (int) job.getBudget() + " received for \"" + job.getTitle() + "\"!");

        Map<String, Object> result = toPaymentResponse(payment, job);
        result.put("message", "Payment of ₹" + (int) job.getBudget() + " completed successfully!");
        return result;
    }

    public Map<String, Object> getPayment(Long jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ApiException("Job not found", 404));

        if (!job.getClientId().equals(userId) &&
                (job.getFreelancerId() == null || !job.getFreelancerId().equals(userId))) {
            throw new ApiException("You do not have access to this payment", 403);
        }

        return paymentRepository.findByJobId(jobId)
                .map(p -> toPaymentResponse(p, job))
                .orElse(Map.of("status", "NOT_PAID", "amount", job.getBudget()));
    }

    public Map<String, Object> toPaymentResponse(Payment payment, Job job) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", payment.getId());
        map.put("jobId", payment.getJobId());
        map.put("amount", payment.getAmount());
        map.put("status", payment.getStatus().name());
        map.put("paidAt", payment.getPaidAt());
        map.put("jobTitle", job.getTitle());
        return map;
    }
}
