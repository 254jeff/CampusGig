package com.campusgig.backend.service;

import com.campusgig.backend.entity.FileAttachment;
import com.campusgig.backend.entity.Job;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.FileAttachmentRepository;
import com.campusgig.backend.repository.JobRepository;
import com.campusgig.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileAttachmentRepository fileRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/zip",
            "text/plain"
    );

    private static final long MAX_SIZE = 10 * 1024 * 1024;

    public FileAttachment uploadFile(String email, Long jobId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File size exceeds 10MB limit");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("File type not allowed: " + file.getContentType());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = null;
        if (jobId != null) {
            job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));
        }

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);

        FileAttachment attachment = FileAttachment.builder()
                .job(job)
                .uploadedBy(user)
                .fileName(file.getOriginalFilename())
                .filePath(uniqueFileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        return fileRepository.save(attachment);
    }

    public List<FileAttachment> getJobFiles(Long jobId) {
        return fileRepository.findByJobIdOrderByCreatedAtAsc(jobId);
    }

    public byte[] getFile(Long id) throws IOException {
        FileAttachment attachment = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Path filePath = Paths.get(uploadDir).resolve(attachment.getFilePath());
        return Files.readAllBytes(filePath);
    }
}
