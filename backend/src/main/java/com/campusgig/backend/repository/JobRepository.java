package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByPostedById(Long userId);

    List<Job> findByCategoryId(Long categoryId);

    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
            "(:category IS NULL OR j.category.id = :category) AND " +
            "(:remote IS NULL OR j.remote = :remote) AND " +
            "(:minBudget IS NULL OR j.budget >= :minBudget) AND " +
            "(:maxBudget IS NULL OR j.budget <= :maxBudget) AND " +
            "(:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findWithFilters(@Param("category") Long category,
                              @Param("remote") Boolean remote,
                              @Param("minBudget") Double minBudget,
                              @Param("maxBudget") Double maxBudget,
                              @Param("search") String search,
                              Pageable pageable);
}