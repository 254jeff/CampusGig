package com.campusgig.backend.service;

import com.campusgig.backend.dto.ApplicationRequest;
import com.campusgig.backend.dto.ApplicationResponse;
import com.campusgig.backend.entity.Application;
import com.campusgig.backend.entity.Job;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.ApplicationRepository;
import com.campusgig.backend.repository.JobRepository;
import com.campusgig.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationResponse applyToJob(String email, Long jobId, ApplicationRequest request) {
        User applicant = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getStatus().equals(Job.JobStatus.OPEN) && !job.getStatus().equals(Job.JobStatus.APPLICATIONS)) {
            throw new RuntimeException("This job is not accepting applications");
        }

        if (job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("You cannot apply to your own job");
        }

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
            throw new RuntimeException("You have already applied to this job");
        }

        Application application = Application.builder()
                .job(job)
                .applicant(applicant)
                .coverMessage(request.getCoverMessage())
                .proposedPrice(request.getProposedPrice())
                .estimatedDays(request.getEstimatedDays())
                .status(Application.ApplicationStatus.PENDING)
                .build();

        return toResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getJobApplications(String email, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only the job poster can view applications");
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getMyApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return applicationRepository.findByApplicantId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse acceptApplication(String email, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only the job poster can accept applications");
        }

        application.setStatus(Application.ApplicationStatus.ACCEPTED);
        application.getJob().setStatus(Job.JobStatus.ASSIGNED);

        applicationRepository.save(application);
        jobRepository.save(application.getJob());

        return toResponse(application);
    }

    public ApplicationResponse rejectApplication(String email, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJob().getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only the job poster can reject applications");
        }

        application.setStatus(Application.ApplicationStatus.REJECTED);
        return toResponse(applicationRepository.save(application));
    }

    public void withdrawApplication(String email, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getApplicant().getEmail().equals(email)) {
            throw new RuntimeException("You can only withdraw your own application");
        }

        if (!application.getStatus().equals(Application.ApplicationStatus.PENDING)) {
            throw new RuntimeException("You can only withdraw pending applications");
        }

        applicationRepository.delete(application);
    }

    private ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .applicantId(application.getApplicant().getId())
                .applicantName(application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName())
                .applicantEmail(application.getApplicant().getEmail())
                .coverMessage(application.getCoverMessage())
                .proposedPrice(application.getProposedPrice())
                .estimatedDays(application.getEstimatedDays())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
