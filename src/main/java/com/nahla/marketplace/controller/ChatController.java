package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.ChatMessageRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.ChatMessageResponse;
import com.nahla.marketplace.dto.response.ChatThreadResponse;
import com.nahla.marketplace.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/api/chat/inbox")
    public ApiResponse<List<ChatThreadResponse>> getInbox(Authentication authentication) {
        return ApiResponse.of(chatService.getUserInbox(authentication.getName()));
    }

    @GetMapping("/api/chat/{threadId}")
    public ApiResponse<List<ChatMessageResponse>> getHistory(@PathVariable String threadId, Authentication authentication) {
        return ApiResponse.of(chatService.getThreadHistory(threadId, authentication.getName()));
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Authentication authentication) {
        ChatMessageResponse savedMessage = chatService.saveMessage(request, authentication.getName());
        messagingTemplate.convertAndSendToUser(
                request.recipientId(), 
                "/queue/messages", 
                savedMessage
        );
    }
}