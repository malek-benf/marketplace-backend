package com.nahla.marketplace.dto.response;
import java.util.Date;
public record ChatThreadResponse(String id, String otherUserId, String otherUserName, String listingId, String lastMessage, Date lastMessageAt) {}