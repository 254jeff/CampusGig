package com.campusgig.backend.service;

import com.campusgig.backend.dto.MessageRequest;
import com.campusgig.backend.dto.MessageResponse;
import com.campusgig.backend.entity.Job;
import com.campusgig.backend.entity.Message;
import com.campusgig.backend.entity.User;
import com.campusgig.backend.repository.JobRepository;
import com.campusgig.backend.repository.MessageRepository;
import com.campusgig.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(String senderEmail, MessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Cannot send message to yourself");
        }

        Message message = Message.builder()
                .job(job)
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .read(false)
                .build();

        return toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getConversation(String email, Long jobId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findConversation(jobId, user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void markAsRead(String email, Long jobId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> unread = messageRepository.findByJobIdOrderByCreatedAtAsc(jobId).stream()
                .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.isRead())
                .collect(Collectors.toList());

        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);
    }

    public int getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findByReceiverIdAndReadFalse(user.getId()).size();
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .jobId(message.getJob().getId())
                .jobTitle(message.getJob().getTitle())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
