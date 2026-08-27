package com.campusgig.backend.service;

import com.campusgig.backend.entity.Application;
import com.campusgig.backend.entity.Job;
import com.campusgig.backend.repository.ApplicationRepository;
import com.campusgig.backend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskWorkflowService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public Job startTask(Long jobId, String email) {
        Job job = getJobAndVerifyOwner(jobId, email);
        validateTransition(job.getStatus(), Job.JobStatus.IN_PROGRESS);
        job.setStatus(Job.JobStatus.IN_PROGRESS);
        return jobRepository.save(job);
    }

    public Job submitTask(Long jobId, String email) {
        Job job = getJobAndVerifyProvider(jobId, email);
        validateTransition(job.getStatus(), Job.JobStatus.SUBMITTED);
        job.setStatus(Job.JobStatus.SUBMITTED);
        return jobRepository.save(job);
    }

    public Job completeTask(Long jobId, String email) {
        Job job = getJobAndVerifyOwner(jobId, email);
        validateTransition(job.getStatus(), Job.JobStatus.COMPLETED);
        job.setStatus(Job.JobStatus.COMPLETED);
        return jobRepository.save(job);
    }

    public Job closeTask(Long jobId, String email) {
        Job job = getJobAndVerifyOwner(jobId, email);
        job.setStatus(Job.JobStatus.CLOSED);
        return jobRepository.save(job);
    }

    public Job disputeTask(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        boolean isOwner = job.getPostedBy().getEmail().equals(email);
        boolean isProvider = getAcceptedApplication(jobId)
                .map(app -> app.getApplicant().getEmail().equals(email))
                .orElse(false);

        if (!isOwner && !isProvider) {
            throw new RuntimeException("Only the job owner or provider can dispute this task");
        }

        job.setStatus(Job.JobStatus.DISPUTED);
        return jobRepository.save(job);
    }

    private Job getJobAndVerifyOwner(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only the job owner can perform this action");
        }

        return job;
    }

    private Job getJobAndVerifyProvider(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return getAcceptedApplication(jobId)
                .filter(app -> app.getApplicant().getEmail().equals(email))
                .map(app -> job)
                .orElseThrow(() -> new RuntimeException("Only the assigned provider can perform this action"));
    }

    private java.util.Optional<Application> getAcceptedApplication(Long jobId) {
        return applicationRepository.findByJobId(jobId).stream()
                .filter(app -> app.getStatus() == Application.ApplicationStatus.ACCEPTED)
                .findFirst();
    }

    private void validateTransition(Job.JobStatus current, Job.JobStatus target) {
        switch (target) {
            case IN_PROGRESS:
                if (current != Job.JobStatus.ASSIGNED) {
                    throw new RuntimeException("Task must be ASSIGNED before starting");
                }
                break;
            case SUBMITTED:
                if (current != Job.JobStatus.IN_PROGRESS) {
                    throw new RuntimeException("Task must be IN_PROGRESS before submitting");
                }
                break;
            case COMPLETED:
                if (current != Job.JobStatus.SUBMITTED) {
                    throw new RuntimeException("Task must be SUBMITTED before completing");
                }
                break;
            default:
                break;
        }
    }
}
