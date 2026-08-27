package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Review;
import com.campusgig.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {
        Review review = reviewService.createReview(
                userDetails.getUsername(),
                ((Number) body.get("jobId")).longValue(),
                ((Number) body.get("reviewedUserId")).longValue(),
                ((Number) body.get("quality")).intValue(),
                ((Number) body.get("communication")).intValue(),
                ((Number) body.get("timeliness")).intValue(),
                (String) body.get("comment")
        );
        return ResponseEntity.ok(review);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }
}