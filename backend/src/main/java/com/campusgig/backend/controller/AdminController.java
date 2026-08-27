package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Category;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.service.AdminService;
import com.campusgig.backend.service.AnalyticsService;
import com.campusgig.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/toggle-enabled")
    public ResponseEntity<User> toggleEnabled(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserEnabled(id));
    }

    @PutMapping("/users/{id}/toggle-verified")
    public ResponseEntity<User> toggleVerified(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserVerified(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<com.campusgig.backend.entity.Job>> getAllJobs() {
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @GetMapping("/analytics/jobs-by-category")
    public ResponseEntity<Map<String, Long>> getJobsByCategory() {
        return ResponseEntity.ok(adminService.getJobsByCategory());
    }

    @GetMapping("/analytics/jobs-by-status")
    public ResponseEntity<Map<String, Long>> getJobsByStatus() {
        return ResponseEntity.ok(adminService.getJobsByStatus());
    }

    @GetMapping("/analytics/top-skills")
    public ResponseEntity<List<Map<String, Object>>> getTopSkills() {
        return ResponseEntity.ok(adminService.getTopSkills());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(categoryService.createCategory(body.get("name"), body.get("description")));
    }

    @GetMapping("/analytics/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(analyticsService.getPlatformOverview());
    }

    @GetMapping("/analytics/jobs-by-month")
    public ResponseEntity<Map<String, Long>> getJobsByMonth(@RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.getJobsByMonth(months));
    }

    @GetMapping("/analytics/applications-by-month")
    public ResponseEntity<Map<String, Long>> getApplicationsByMonth(@RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.getApplicationsByMonth(months));
    }

    @GetMapping("/analytics/completion-rate")
    public ResponseEntity<Map<String, Double>> getCompletionRate() {
        Map<String, Double> result = new HashMap<>();
        result.put("completionRate", analyticsService.getCompletionRate());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/analytics/top-universities")
    public ResponseEntity<Map<String, Object>> getTopUniversities() {
        return ResponseEntity.ok(analyticsService.getTopUniversities());
    }

    @GetMapping("/analytics/top-courses")
    public ResponseEntity<Map<String, Object>> getTopCourses() {
        return ResponseEntity.ok(analyticsService.getTopCourses());
    }

    @GetMapping("/analytics/most-requested-skills")
    public ResponseEntity<List<Map<String, Object>>> getMostRequestedSkills() {
        return ResponseEntity.ok(analyticsService.getMostRequestedSkills());
    }
}