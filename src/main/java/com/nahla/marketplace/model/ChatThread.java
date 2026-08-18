package com.nahla.marketplace.model;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "chat_threads")
public class ChatThread {
    @Id
    private String id;
    private List<String> participantIds; 
    private String listingId; 
    private String lastMessage;
    private Date lastMessageAt;
    private int unreadCount; 
}