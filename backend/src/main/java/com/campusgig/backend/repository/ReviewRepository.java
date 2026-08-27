package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByJobId(Long jobId);
    List<Review> findByReviewedUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByJobId(Long jobId);
    boolean existsByJobIdAndReviewerId(Long jobId, Long reviewerId);
}
