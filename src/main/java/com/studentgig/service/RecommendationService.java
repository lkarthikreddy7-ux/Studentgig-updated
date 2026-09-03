package com.studentgig.service;

import com.studentgig.model.Job;
import com.studentgig.model.JobStatus;
import com.studentgig.model.User;
import com.studentgig.repository.JobRepository;
import com.studentgig.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Recommends open jobs using a simple skill-matching algorithm.
 *
 * DSA used:
 * - HashSet for fast average-case skill membership checks.
 * - ArrayList/List for dynamic collections of jobs and recommendations.
 * - Sorting with Comparator to rank recommendations by match percentage.
 *
 * For each job, the algorithm counts how many required skills are present
 * in the student's skill set and calculates a percentage.
 */
@Service
public class RecommendationService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobService jobService;

    public RecommendationService(JobRepository jobRepository,
                                 UserRepository userRepository,
                                 JobService jobService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobService = jobService;
    }

    public List<Map<String, Object>> getRecommendedJobs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // HashSet gives fast average O(1) membership checks for skills.
        HashSet<String> studentSkills = user.getSkills().stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .map(this::normalize)
                .collect(Collectors.toCollection(HashSet::new));

        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (Job job : jobRepository.findAll()) {
            if (job.getStatus() != JobStatus.OPEN) {
                continue;
            }

            int requiredSkills = 0;
            int matchingSkills = 0;

            for (String skill : job.getSkillsRequired()) {
                if (skill == null || skill.isBlank()) {
                    continue;
                }
                requiredSkills++;
                if (studentSkills.contains(normalize(skill))) {
                    matchingSkills++;
                }
            }

            double matchPercentage = requiredSkills == 0
                    ? 0.0
                    : (matchingSkills * 100.0) / requiredSkills;

            Map<String, Object> recommendation = jobService.toJobResponse(job);
            recommendation.put("matchingSkills", matchingSkills);
            recommendation.put("requiredSkills", requiredSkills);
            recommendation.put("matchPercentage", Math.round(matchPercentage * 10.0) / 10.0);
            recommendations.add(recommendation);
        }

        // Rank best skill matches first; newest jobs break ties.
        recommendations.sort(
                Comparator.comparingDouble((Map<String, Object> item) ->
                                ((Number) item.get("matchPercentage")).doubleValue())
                        .reversed()
                        .thenComparing(item -> String.valueOf(item.get("createdAt")), Comparator.reverseOrder())
        );

        return recommendations;
    }

    private String normalize(String skill) {
        return skill.trim().toLowerCase(Locale.ROOT);
    }
}
