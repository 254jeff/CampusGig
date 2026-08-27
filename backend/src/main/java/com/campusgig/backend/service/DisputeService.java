package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Dispute openDispute(String email, Long jobId, String reason, String description) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (disputeRepository.findByJobId(jobId).isPresent()) {
            throw new RuntimeException("Dispute already exists for this job");
        }

        Dispute dispute = Dispute.builder()
                .job(job)
                .openedBy(user)
                .reason(reason)
                .description(description)
                .build();

        job.setStatus(Job.JobStatus.DISPUTED);
        jobRepository.save(job);

        return disputeRepository.save(dispute);
    }

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Dispute> getOpenDisputes() {
        return disputeRepository.findByStatusOrderByCreatedAtDesc(Dispute.DisputeStatus.OPEN);
    }

    public Dispute resolveDispute(Long disputeId, String resolution, String adminEmail) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        dispute.setResolution(resolution);
        dispute.setResolvedBy(admin);
        dispute.setStatus(Dispute.DisputeStatus.RESOLVED);
        return disputeRepository.save(dispute);
    }
}
