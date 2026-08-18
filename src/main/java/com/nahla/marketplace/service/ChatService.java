package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.request.ChatMessageRequest;
import com.nahla.marketplace.dto.response.ChatMessageResponse;
import com.nahla.marketplace.dto.response.ChatThreadResponse;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.ChatThread;
import com.nahla.marketplace.model.Message;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.ChatThreadRepository;
import com.nahla.marketplace.repository.MessageRepository;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // Added

    public ChatService(ChatThreadRepository threadRepository, MessageRepository messageRepository, UserRepository userRepository, NotificationService notificationService) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<ChatThreadResponse> getUserInbox(String userPhone) {
        User user = userRepository.findByPhone(userPhone).orElseThrow();
        List<ChatThread> threads = threadRepository.findByParticipantIdsContainingOrderByLastMessageAtDesc(user.getId());
        
        return threads.stream().map(thread -> {
            String otherUserId = thread.getParticipantIds().stream()
                    .filter(id -> !id.equals(user.getId()))
                    .findFirst().orElse(null);
            
            String otherUserName = userRepository.findById(otherUserId)
                    .map(User::getName).orElse("Unknown User");

            return new ChatThreadResponse(
                    thread.getId(), otherUserId, otherUserName, 
                    thread.getListingId(), thread.getLastMessage(), thread.getLastMessageAt()
            );
        }).collect(Collectors.toList());
    }

    public List<ChatMessageResponse> getThreadHistory(String threadId, String userPhone) {
        User user = userRepository.findByPhone(userPhone).orElseThrow();
        ChatThread thread = threadRepository.findById(threadId).orElseThrow();
        
        if (!thread.getParticipantIds().contains(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        
        return messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId)
                .stream().map(ChatMessageResponse::from).collect(Collectors.toList());
    }

    public ChatMessageResponse saveMessage(ChatMessageRequest request, String senderPhone) {
        User sender = userRepository.findByPhone(senderPhone).orElseThrow();
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", request.recipientId()));

        ChatThread thread = threadRepository.findByParticipantsAndListing(sender.getId(), recipient.getId(), request.listingId())
                .orElseGet(() -> {
                    ChatThread newThread = ChatThread.builder()
                            .participantIds(List.of(sender.getId(), recipient.getId()))
                            .listingId(request.listingId())
                            .lastMessageAt(new Date())
                            .build();
                    return threadRepository.save(newThread);
                });

        Message message = Message.builder()
                .threadId(thread.getId())
                .senderId(sender.getId())
                .content(request.content())
                .createdAt(new Date())
                .isRead(false)
                .build();
        message = messageRepository.save(message);

        thread.setLastMessage(message.getContent());
        thread.setLastMessageAt(message.getCreatedAt());
        threadRepository.save(thread);

        if (recipient.getFcmTokens() != null && !recipient.getFcmTokens().isEmpty()) {
            notificationService.sendMulticastNotification(
                recipient.getFcmTokens(), 
                "New Message from " + sender.getName(), 
                message.getContent()
            );
        }

        return ChatMessageResponse.from(message);
    }
}