package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Job;
import com.campusgig.backend.service.JobService;
import com.campusgig.backend.service.TaskWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs/{jobId}")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskWorkflowController {

    private final TaskWorkflowService workflowService;
    private final JobService jobService;

    @PutMapping("/start")
    public ResponseEntity<?> startTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        Job job = workflowService.startTask(jobId, userDetails.getUsername());
        return ResponseEntity.ok(jobService.toResponse(job));
    }

    @PutMapping("/submit")
    public ResponseEntity<?> submitTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        Job job = workflowService.submitTask(jobId, userDetails.getUsername());
        return ResponseEntity.ok(jobService.toResponse(job));
    }

    @PutMapping("/complete")
    public ResponseEntity<?> completeTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        Job job = workflowService.completeTask(jobId, userDetails.getUsername());
        return ResponseEntity.ok(jobService.toResponse(job));
    }

    @PutMapping("/close")
    public ResponseEntity<?> closeTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        Job job = workflowService.closeTask(jobId, userDetails.getUsername());
        return ResponseEntity.ok(jobService.toResponse(job));
    }

    @PutMapping("/dispute")
    public ResponseEntity<?> disputeTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        Job job = workflowService.disputeTask(jobId, userDetails.getUsername());
        return ResponseEntity.ok(jobService.toResponse(job));
    }
}