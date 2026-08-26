package com.campusgig.backend.controller;

import com.campusgig.backend.dto.JobRequest;
import com.campusgig.backend.dto.JobResponse;
import com.campusgig.backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> getOpenJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(jobService.getOpenJobs(page, size, sort));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(jobService.searchJobs(page, size, sort, category, remote, minBudget, maxBudget, search));
    }

    @GetMapping("/my")
    public ResponseEntity<java.util.List<JobResponse>> getMyJobs(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getMyJobs(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, userDetails.getUsername(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobService.deleteJob(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
