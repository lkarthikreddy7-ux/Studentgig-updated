package com.studentgig.repository;

import com.studentgig.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PaymentRepository {
    private final Map<Long, Payment> paymentsById = new HashMap<>();
    private final Map<Long, Payment> paymentsByJobId = new HashMap<>();
    private long nextId = 1;

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(nextId++);
        }
        paymentsById.put(payment.getId(), payment);
        paymentsByJobId.put(payment.getJobId(), payment);
        return payment;
    }

    public Optional<Payment> findByJobId(Long jobId) {
        return Optional.ofNullable(paymentsByJobId.get(jobId));
    }
}
