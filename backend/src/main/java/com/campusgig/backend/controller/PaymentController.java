package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Payment;
import com.campusgig.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate/{jobId}")
    public ResponseEntity<Payment> initiatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(paymentService.initiatePayment(userDetails.getUsername(), jobId));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.confirmPayment(id));
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<Payment> failPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.failPayment(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Payment>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getMyPayments(userDetails.getUsername()));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<Payment> getByJobId(@PathVariable Long jobId) {
        return ResponseEntity.ok(paymentService.getByJobId(jobId));
    }

    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Double>> getEarnings(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Double> result = new HashMap<>();
        result.put("totalEarnings", paymentService.getTotalEarnings(userDetails.getUsername()));
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
