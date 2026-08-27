package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ReportRepository reportRepository;
    private final DisputeRepository disputeRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final StudentProfileRepository profileRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalJobs", jobRepository.count());
        stats.put("activeJobs", jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == Job.JobStatus.OPEN ||
                        j.getStatus() == Job.JobStatus.ASSIGNED ||
                        j.getStatus() == Job.JobStatus.IN_PROGRESS)
                .count());
        stats.put("completedJobs", jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == Job.JobStatus.COMPLETED)
                .count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("pendingReports", reportRepository.findByStatusOrderByCreatedAtDesc(Report.ReportStatus.PENDING).size());
        stats.put("openDisputes", disputeRepository.findByStatusOrderByCreatedAtDesc(Dispute.DisputeStatus.OPEN).size());
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User toggleUserEnabled(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public User toggleUserVerified(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(!user.isVerified());
        return userRepository.save(user);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Map<String, Long> getJobsByCategory() {
        Map<String, Long> counts = new HashMap<>();
        categoryRepository.findAll().forEach(cat -> {
            long count = jobRepository.findAll().stream()
                    .filter(j -> j.getCategory() != null && j.getCategory().getId().equals(cat.getId()))
                    .count();
            counts.put(cat.getName(), count);
        });
        return counts;
    }

    public Map<String, Long> getJobsByStatus() {
        Map<String, Long> counts = new HashMap<>();
        for (Job.JobStatus status : Job.JobStatus.values()) {
            long count = jobRepository.findAll().stream()
                    .filter(j -> j.getStatus() == status)
                    .count();
            counts.put(status.name(), count);
        }
        return counts;
    }

    public List<Map<String, Object>> getTopSkills() {
        return skillRepository.findAll().stream()
                .map(skill -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", skill.getName());
                    map.put("count", profileRepository.findAll().stream()
                            .filter(p -> p.getSkills() != null && p.getSkills().stream()
                                    .anyMatch(s -> s.getId().equals(skill.getId())))
                            .count());
                    return map;
                })
                .sorted((a, b) -> ((Long) b.get("count")).compareTo((Long) a.get("count")))
                .limit(10)
                .toList();
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
