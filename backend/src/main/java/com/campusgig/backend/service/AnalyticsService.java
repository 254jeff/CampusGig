package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final ReviewRepository reviewRepository;
    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;

    public Map<String, Object> getPlatformOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalUsers", userRepository.count());
        overview.put("totalStudents", userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT).count());
        overview.put("totalJobs", jobRepository.count());
        overview.put("completedJobs", jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == Job.JobStatus.COMPLETED).count());
        overview.put("totalApplications", applicationRepository.count());
        overview.put("totalReviews", reviewRepository.count());
        overview.put("averageRating", profileRepository.findAll().stream()
                .filter(p -> p.getRating() != null && p.getRating() > 0)
                .mapToDouble(StudentProfile::getRating)
                .average()
                .orElse(0.0));
        return overview;
    }

    public Map<String, Long> getJobsByMonth(int months) {
        Map<String, Long> counts = new LinkedHashMap<>();
        LocalDateTime start = LocalDateTime.now().minusMonths(months);
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthStart = start.plusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1);
            String key = monthStart.getMonth().toString() + " " + monthStart.getYear();
            long count = jobRepository.findAll().stream()
                    .filter(j -> j.getCreatedAt() != null &&
                            j.getCreatedAt().isAfter(monthStart) &&
                            j.getCreatedAt().isBefore(monthEnd))
                    .count();
            counts.put(key, count);
        }
        return counts;
    }

    public Map<String, Long> getApplicationsByMonth(int months) {
        Map<String, Long> counts = new LinkedHashMap<>();
        LocalDateTime start = LocalDateTime.now().minusMonths(months);
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime monthStart = start.plusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1);
            String key = monthStart.getMonth().toString() + " " + monthStart.getYear();
            long count = applicationRepository.findAll().stream()
                    .filter(a -> a.getCreatedAt() != null &&
                            a.getCreatedAt().isAfter(monthStart) &&
                            a.getCreatedAt().isBefore(monthEnd))
                    .count();
            counts.put(key, count);
        }
        return counts;
    }

    public double getCompletionRate() {
        long completed = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == Job.JobStatus.COMPLETED).count();
        long total = jobRepository.count();
        return total == 0 ? 0 : Math.round((double) completed / total * 100.0) / 100.0;
    }

    public Map<String, Long> getTopUniversities() {
        Map<String, Long> grouped = profileRepository.findAll().stream()
                .filter(p -> p.getUniversity() != null)
                .collect(Collectors.groupingBy(StudentProfile::getUniversity, Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> getTopCourses() {
        Map<String, Long> grouped = profileRepository.findAll().stream()
                .filter(p -> p.getCourse() != null)
                .collect(Collectors.groupingBy(StudentProfile::getCourse, Collectors.counting()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public List<Map<String, Object>> getMostRequestedSkills() {
        Map<String, Long> skillCounts = new HashMap<>();
        jobRepository.findAll().forEach(job -> {
            if (job.getRequiredSkills() != null) {
                job.getRequiredSkills().forEach(skill ->
                        skillCounts.merge(skill.getName(), 1L, Long::sum));
            }
        });
        return skillCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(15)
                .map(e -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("skill", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}