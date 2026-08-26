package com.campusgig.backend.service;

import com.campusgig.backend.dto.JobRequest;
import com.campusgig.backend.dto.JobResponse;
import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SkillService skillService;
    private final com.campusgig.backend.repository.ApplicationRepository applicationRepository;

    public JobResponse createJob(String email, JobRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        List<Skill> skills = request.getSkillIds() != null
                ? skillService.getSkillsByIds(request.getSkillIds())
                : List.of();

        Job job = Job.builder()
                .postedBy(user)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(skills.stream().collect(Collectors.toSet()))
                .budget(request.getBudget())
                .taskType(request.getTaskType())
                .location(request.getLocation())
                .remote(request.getRemote() != null ? request.getRemote() : false)
                .deadline(request.getDeadline())
                .status(Job.JobStatus.OPEN)
                .build();

        return toResponse(jobRepository.save(job));
    }

    public Page<JobResponse> getOpenJobs(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "createdAt").descending());
        return jobRepository.findByStatus(Job.JobStatus.OPEN, pageable).map(this::toResponse);
    }

    public Page<JobResponse> searchJobs(int page, int size, String sort,
                                        Long category, Boolean remote,
                                        Double minBudget, Double maxBudget,
                                        String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort != null ? sort : "createdAt").descending());
        return jobRepository.findWithFilters(category, remote, minBudget, maxBudget, search, pageable)
                .map(this::toResponse);
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return toResponse(job);
    }

    public List<JobResponse> getMyJobs(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jobRepository.findByPostedById(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public JobResponse updateJob(Long id, String email, JobRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("You can only edit your own jobs");
        }

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getBudget() != null) job.setBudget(request.getBudget());
        if (request.getTaskType() != null) job.setTaskType(request.getTaskType());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getRemote() != null) job.setRemote(request.getRemote());
        if (request.getDeadline() != null) job.setDeadline(request.getDeadline());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            job.setCategory(category);
        }

        if (request.getSkillIds() != null) {
            List<Skill> skills = skillService.getSkillsByIds(request.getSkillIds());
            job.setRequiredSkills(skills.stream().collect(Collectors.toSet()));
        }

        return toResponse(jobRepository.save(job));
    }

    public void deleteJob(Long id, String email) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("You can only delete your own jobs");
        }

        jobRepository.delete(job);
    }

    private JobResponse toResponse(Job job) {
        int applicationCount = applicationRepository.countByJobId(job.getId());

        List<JobResponse.SkillDto> skillDtos = job.getRequiredSkills() != null
                ? job.getRequiredSkills().stream()
                .map(skill -> JobResponse.SkillDto.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .build())
                .collect(Collectors.toList())
                : List.of();

        JobResponse.CategoryDto categoryDto = job.getCategory() != null
                ? JobResponse.CategoryDto.builder()
                .id(job.getCategory().getId())
                .name(job.getCategory().getName())
                .build()
                : null;

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .budget(job.getBudget())
                .taskType(job.getTaskType())
                .location(job.getLocation())
                .remote(job.isRemote())
                .deadline(job.getDeadline())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .postedById(job.getPostedBy().getId())
                .postedByName(job.getPostedBy().getFirstName() + " " + job.getPostedBy().getLastName())
                .category(categoryDto)
                .requiredSkills(skillDtos)
                .applicationCount(applicationCount)
                .build();
    }
}