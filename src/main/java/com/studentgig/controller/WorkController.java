package com.studentgig.controller;

import com.studentgig.model.WorkSubmission;
import com.studentgig.service.AuthService;
import com.studentgig.service.PaymentService;
import com.studentgig.service.WorkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WorkController {
    private final WorkService workService;
    private final PaymentService paymentService;
    private final AuthService authService;

    public WorkController(WorkService workService, PaymentService paymentService, AuthService authService) {
        this.workService = workService;
        this.paymentService = paymentService;
        this.authService = authService;
    }

    @PostMapping("/api/jobs/{jobId}/submission")
    public ResponseEntity<Map<String, Object>> submitWork(@PathVariable Long jobId,
                                                         @RequestBody WorkSubmission submission,
                                                         HttpSession session) {
        var user = authService.requireUser(session);
        Map<String, Object> result = workService.submitWork(jobId, submission, user);
        result.put("message", "Work submitted successfully.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/jobs/{jobId}/submission")
    public ResponseEntity<Map<String, Object>> getSubmission(@PathVariable Long jobId,
                                                               HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(workService.getSubmission(jobId, userId));
    }

    @PutMapping("/api/jobs/{jobId}/accept-work")
    public ResponseEntity<Map<String, Object>> acceptWork(@PathVariable Long jobId,
                                                          HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(workService.acceptWork(jobId, userId));
    }

    @PutMapping("/api/jobs/{jobId}/request-changes")
    public ResponseEntity<Map<String, Object>> requestChanges(@PathVariable Long jobId,
                                                              @RequestBody Map<String, String> body,
                                                              HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        String feedback = body.get("feedback");
        return ResponseEntity.ok(workService.requestChanges(jobId, userId, feedback));
    }

    @GetMapping("/api/jobs/{jobId}/payment")
    public ResponseEntity<Map<String, Object>> getPayment(@PathVariable Long jobId,
                                                          HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(paymentService.getPayment(jobId, userId));
    }
}
