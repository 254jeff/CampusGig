package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final StudentProfileRepository profileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public List<Map<String, Object>> findMatches(Long jobId, int limit) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        List<StudentProfile> profiles = profileRepository.findAll().stream()
                .filter(p -> p.isAvailable())
                .filter(p -> !p.getUser().getId().equals(job.getPostedBy().getId()))
                .filter(p -> !applicationRepository.existsByJobIdAndApplicantId(jobId, p.getUser().getId()))
                .collect(Collectors.toList());

        return profiles.stream()
                .map(profile -> {
                    Map<String, Object> match = new LinkedHashMap<>();
                    match.put("userId", profile.getUser().getId());
                    match.put("firstName", profile.getUser().getFirstName());
                    match.put("lastName", profile.getUser().getLastName());
                    match.put("avatarUrl", profile.getAvatarUrl());
                    match.put("university", profile.getUniversity());
                    match.put("course", profile.getCourse());
                    match.put("rating", profile.getRating());
                    match.put("completedTasks", profile.getCompletedTasks());
                    match.put("skills", profile.getSkills().stream()
                            .map(Skill::getName).collect(Collectors.toList()));
                    match.put("matchScore", calculateMatchScore(job, profile));
                    return match;
                })
                .sorted((a, b) -> ((Double) b.get("matchScore")).compareTo((Double) a.get("matchScore")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double calculateMatchScore(Job job, StudentProfile profile) {
        double skillScore = calculateSkillMatch(job, profile);
        double ratingScore = calculateRatingScore(profile);
        double experienceScore = calculateExperienceScore(profile);
        double availabilityScore = profile.isAvailable() ? 1.0 : 0.0;

        double total = (skillScore * 0.50) +
                (ratingScore * 0.25) +
                (experienceScore * 0.15) +
                (availabilityScore * 0.10);

        return Math.round(total * 100.0) / 100.0;
    }

    private double calculateSkillMatch(Job job, StudentProfile profile) {
        if (job.getRequiredSkills() == null || job.getRequiredSkills().isEmpty()) return 0.5;
        if (profile.getSkills() == null || profile.getSkills().isEmpty()) return 0.0;

        Set<Long> requiredIds = job.getRequiredSkills().stream()
                .map(Skill::getId).collect(Collectors.toSet());
        Set<Long> studentIds = profile.getSkills().stream()
                .map(Skill::getId).collect(Collectors.toSet());

        long matched = requiredIds.stream().filter(studentIds::contains).count();
        return (double) matched / requiredIds.size();
    }

    private double calculateRatingScore(StudentProfile profile) {
        if (profile.getRating() == null || profile.getRating() == 0) return 0.3;
        return Math.min(profile.getRating() / 5.0, 1.0);
    }

    private double calculateExperienceScore(StudentProfile profile) {
        if (profile.getCompletedTasks() == null || profile.getCompletedTasks() == 0) return 0.2;
        if (profile.getCompletedTasks() >= 20) return 1.0;
        return (double) profile.getCompletedTasks() / 20.0;
    }
}
