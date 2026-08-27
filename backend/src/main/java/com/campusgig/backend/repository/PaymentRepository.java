package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByJobId(Long jobId);
    List<Payment> findByPayerIdOrderByCreatedAtDesc(Long payerId);
    List<Payment> findByPayeeIdOrderByCreatedAtDesc(Long payeeId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.payee.id = :payeeId AND p.status = 'PAID'")
    double sumEarningsByPayeeId(@Param("payeeId") Long payeeId);

    long countByStatus(Payment.PaymentStatus status);
}