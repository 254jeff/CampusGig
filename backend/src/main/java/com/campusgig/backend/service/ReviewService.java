package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;

    public Review createReview(String email, Long jobId, Long reviewedUserId,
                               int quality, int communication, int timeliness, String comment) {
        if (quality < 1 || quality > 5 || communication < 1 || communication > 5 || timeliness < 1 || timeliness > 5) {
            throw new RuntimeException("Ratings must be between 1 and 5");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != Job.JobStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed jobs");
        }

        if (reviewRepository.existsByJobIdAndReviewerId(jobId, getCurrentUserId(email))) {
            throw new RuntimeException("You have already reviewed this job");
        }

        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User reviewedUser = userRepository.findById(reviewedUserId)
                .orElseThrow(() -> new RuntimeException("Reviewed user not found"));

        Review review = Review.builder()
                .job(job)
                .reviewer(reviewer)
                .reviewedUser(reviewedUser)
                .quality(quality)
                .communication(communication)
                .timeliness(timeliness)
                .comment(comment)
                .build();

        reviewRepository.save(review);
        updateUserRating(reviewedUser.getId());
        return review;
    }

    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
    }

    private void updateUserRating(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
        if (reviews.isEmpty()) return;

        double avgQuality = reviews.stream().mapToInt(Review::getQuality).average().orElse(0);
        double avgComm = reviews.stream().mapToInt(Review::getCommunication).average().orElse(0);
        double avgTime = reviews.stream().mapToInt(Review::getTimeliness).average().orElse(0);
        double overall = (avgQuality + avgComm + avgTime) / 3.0;

        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setRating(Math.round(overall * 10.0) / 10.0);
            profile.setCompletedTasks(reviews.size());
            profileRepository.save(profile);
        });
    }

    private Long getCurrentUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }
}