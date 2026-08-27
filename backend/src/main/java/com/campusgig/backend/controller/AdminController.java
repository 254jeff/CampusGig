package com.campusgig.backend.controller;

import com.campusgig.backend.entity.Category;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.service.AdminService;
import com.campusgig.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

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
}
