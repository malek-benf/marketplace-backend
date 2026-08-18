package com.nahla.marketplace.dto.response;
import com.nahla.marketplace.model.Message;
import java.util.Date;
public record ChatMessageResponse(String id, String threadId, String senderId, String content, Date createdAt) {
    public static ChatMessageResponse from(Message msg) {
        return new ChatMessageResponse(msg.getId(), msg.getThreadId(), msg.getSenderId(), msg.getContent(), msg.getCreatedAt());
    }
}