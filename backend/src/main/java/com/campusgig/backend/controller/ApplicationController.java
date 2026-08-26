package com.campusgig.backend.controller;

import com.campusgig.backend.dto.ApplicationRequest;
import com.campusgig.backend.dto.ApplicationResponse;
import com.campusgig.backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApplicationResponse> applyToJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyToJob(userDetails.getUsername(), jobId, request));
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> getJobApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getJobApplications(userDetails.getUsername(), jobId));
    }

    @GetMapping("/applications/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(applicationService.getMyApplications(userDetails.getUsername()));
    }

    @PutMapping("/applications/{id}/accept")
    public ResponseEntity<ApplicationResponse> acceptApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(applicationService.acceptApplication(userDetails.getUsername(), id));
    }

    @PutMapping("/applications/{id}/reject")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(applicationService.rejectApplication(userDetails.getUsername(), id));
    }

    @DeleteMapping("/applications/{id}/withdraw")
    public ResponseEntity<Void> withdrawApplication(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        applicationService.withdrawApplication(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
