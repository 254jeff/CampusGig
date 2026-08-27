package com.campusgig.backend.repository;

import com.campusgig.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByJobIdOrderByCreatedAtAsc(Long jobId);

    @Query("SELECT m FROM Message m WHERE m.job.id = :jobId AND " +
            "(m.sender.id = :userId OR m.receiver.id = :userId) " +
            "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("jobId") Long jobId, @Param("userId") Long userId);

    int countByJobIdAndReceiverIdAndReadFalse(Long jobId, Long receiverId);

    List<Message> findByReceiverIdAndReadFalse(Long receiverId);
}
