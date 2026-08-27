package com.campusgig.backend.controller;

import com.campusgig.backend.dto.MessageRequest;
import com.campusgig.backend.dto.MessageResponse;
import com.campusgig.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(userDetails.getUsername(), request));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(messageService.getConversation(userDetails.getUsername(), jobId));
    }

    @PutMapping("/job/{jobId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long jobId) {
        messageService.markAsRead(userDetails.getUsername(), jobId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        int count = messageService.getUnreadCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("count", count));
    }
}
