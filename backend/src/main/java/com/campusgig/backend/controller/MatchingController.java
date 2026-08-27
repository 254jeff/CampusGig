package com.campusgig.backend.controller;

import com.campusgig.backend.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Map<String, Object>>> findMatches(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(matchingService.findMatches(jobId, limit));
    }
}
