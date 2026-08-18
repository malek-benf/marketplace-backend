package com.nahla.marketplace.model;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Builder
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String threadId;
    private String senderId;
    private String content;
    private Date createdAt;
    private boolean isRead;
}