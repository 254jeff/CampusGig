package com.campusgig.backend.service;

import com.campusgig.backend.entity.*;
import com.campusgig.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public Payment initiatePayment(String email, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().getEmail().equals(email)) {
            throw new RuntimeException("Only the job poster can initiate payment");
        }

        if (job.getStatus() != Job.JobStatus.COMPLETED) {
            throw new RuntimeException("Payment can only be initiated for completed jobs");
        }

        if (paymentRepository.findByJobId(jobId).isPresent()) {
            throw new RuntimeException("Payment already initiated for this job");
        }

        User payee = applicationRepository.findByJobId(jobId).stream()
                .filter(a -> a.getStatus() == Application.ApplicationStatus.ACCEPTED)
                .findFirst()
                .map(Application::getApplicant)
                .orElseThrow(() -> new RuntimeException("No accepted applicant found"));

        Payment payment = Payment.builder()
                .job(job)
                .payer(job.getPostedBy())
                .payee(payee)
                .amount(job.getBudget())
                .status(Payment.PaymentStatus.PENDING)
                .method("MANUAL")
                .reference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not pending");
        }

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        return paymentRepository.save(payment);
    }

    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PAID) {
            throw new RuntimeException("Only paid payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    public Payment failPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public List<Payment> getMyPayments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Payment> asPayer = paymentRepository.findByPayerIdOrderByCreatedAtDesc(user.getId());
        List<Payment> asPayee = paymentRepository.findByPayeeIdOrderByCreatedAtDesc(user.getId());
        asPayer.addAll(asPayee);
        return asPayer;
    }

    public Payment getByJobId(Long jobId) {
        return paymentRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("No payment found for this job"));
    }

    public double getTotalEarnings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentRepository.sumEarningsByPayeeId(user.getId());
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
