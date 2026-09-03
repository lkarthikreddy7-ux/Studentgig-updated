package com.studentgig.controller;

import com.studentgig.model.Proposal;
import com.studentgig.service.AuthService;
import com.studentgig.service.ProposalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProposalController {
    private final ProposalService proposalService;
    private final AuthService authService;

    public ProposalController(ProposalService proposalService, AuthService authService) {
        this.proposalService = proposalService;
        this.authService = authService;
    }

    @PostMapping("/api/jobs/{jobId}/proposals")
    public ResponseEntity<Map<String, Object>> submitProposal(@PathVariable Long jobId,
                                                                @RequestBody Proposal proposal,
                                                                HttpSession session) {
        var user = authService.requireUser(session);
        Map<String, Object> result = proposalService.submitProposal(jobId, proposal, user);
        result.put("message", "Proposal submitted successfully.");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/jobs/{jobId}/proposals")
    public ResponseEntity<List<Map<String, Object>>> getProposals(@PathVariable Long jobId,
                                                                  HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(proposalService.getProposalsForJob(jobId, userId));
    }

    @GetMapping("/api/proposals/my")
    public ResponseEntity<List<Map<String, Object>>> getMyProposals(HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(proposalService.getMyProposals(userId));
    }

    @PutMapping("/api/proposals/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptProposal(@PathVariable Long id,
                                                                HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(proposalService.acceptProposal(id, userId));
    }

    @PutMapping("/api/proposals/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectProposal(@PathVariable Long id,
                                                              HttpSession session) {
        Long userId = authService.requireUser(session).getId();
        return ResponseEntity.ok(proposalService.rejectProposal(id, userId));
    }
}
