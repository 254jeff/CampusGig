package com.campusgig.backend.controller;

import com.campusgig.backend.entity.FileAttachment;
import com.campusgig.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<FileAttachment> uploadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long jobId) throws IOException {
        return ResponseEntity.ok(fileUploadService.uploadFile(userDetails.getUsername(), jobId, file));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<FileAttachment>> getJobFiles(@PathVariable Long jobId) {
        return ResponseEntity.ok(fileUploadService.getJobFiles(jobId));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws IOException {
        FileAttachment attachment = fileUploadService.getJobFiles(id).stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(null);
        byte[] data = fileUploadService.getFile(id);
        String fileName = "file";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}