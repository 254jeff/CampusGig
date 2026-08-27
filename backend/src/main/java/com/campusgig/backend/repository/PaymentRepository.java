package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByJobId(Long jobId);
    List<Payment> findByPayerIdOrderByCreatedAtDesc(Long payerId);
    List<Payment> findByPayeeIdOrderByCreatedAtDesc(Long payeeId);
    double sumByPayeeIdAndStatus(Long payeeId, Payment.PaymentStatus status);
    long countByStatus(Payment.PaymentStatus status);
}
