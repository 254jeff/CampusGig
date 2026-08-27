package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Dispute;
import com.campusgig.backend.service.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public ResponseEntity<Dispute> openDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(disputeService.openDispute(
                userDetails.getUsername(),
                ((Number) body.get("jobId")).longValue(),
                (String) body.get("reason"),
                (String) body.get("description")
        ));
    }

    @GetMapping
    public ResponseEntity<List<Dispute>> getAllDisputes() {
        return ResponseEntity.ok(disputeService.getAllDisputes());
    }

    @GetMapping("/open")
    public ResponseEntity<List<Dispute>> getOpenDisputes() {
        return ResponseEntity.ok(disputeService.getOpenDisputes());
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Dispute> resolveDispute(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(disputeService.resolveDispute(
                id,
                (String) body.get("resolution"),
                userDetails.getUsername()
        ));
    }
}
