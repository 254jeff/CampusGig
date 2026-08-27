package com.campusgig.backend.repository;

import com.campusgig.backend.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByJobId(Long jobId);
    List<FileAttachment> findByJobIdOrderByCreatedAtAsc(Long jobId);
}