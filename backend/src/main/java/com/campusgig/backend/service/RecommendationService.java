package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final JobRepository jobRepository;
    private final StudentProfileRepository profileRepository;
    private final ApplicationRepository applicationRepository;
    private final MatchingService matchingService;

    public List<Map<String, Object>> getRecommendedJobs(String email, int limit) {
        StudentProfile profile = profileRepository.findAll().stream()
                .filter(p -> p.getUser().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<Job> openJobs = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == Job.JobStatus.OPEN)
                .filter(j -> !j.getPostedBy().getEmail().equals(email))
                .filter(j -> !applicationRepository.existsByJobIdAndApplicantId(j.getId(), profile.getUser().getId()))
                .collect(Collectors.toList());

        return openJobs.stream()
                .map(job -> {
                    Map<String, Object> recommendation = new LinkedHashMap<>();
                    recommendation.put("jobId", job.getId());
                    recommendation.put("title", job.getTitle());
                    recommendation.put("description", job.getDescription());
                    recommendation.put("budget", job.getBudget());
                    recommendation.put("location", job.getLocation());
                    recommendation.put("remote", job.isRemote());
                    recommendation.put("deadline", job.getDeadline());
                    recommendation.put("category", job.getCategory() != null ? job.getCategory().getName() : null);
                    recommendation.put("requiredSkills", job.getRequiredSkills().stream()
                            .map(Skill::getName).collect(Collectors.toList()));
                    recommendation.put("matchScore", matchingService.calculateMatchScore(job, profile));
                    recommendation.put("postedByName", job.getPostedBy().getFirstName() + " " + job.getPostedBy().getLastName());
                    return recommendation;
                })
                .sorted((a, b) -> ((Double) b.get("matchScore")).compareTo((Double) a.get("matchScore")))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
