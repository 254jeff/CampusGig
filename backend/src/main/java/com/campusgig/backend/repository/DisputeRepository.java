package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Optional<Dispute> findByJobId(Long jobId);
    List<Dispute> findByStatusOrderByCreatedAtDesc(Dispute.DisputeStatus status);
    List<Dispute> findAllByOrderByCreatedAtDesc();
}
