package com.nahla.marketplace.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    // Batch send notifications to multiple users/devices
    public void sendMulticastNotification(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) return;

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println("Push notification sent to " + tokens.size() + " devices.");
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send push notification: " + e.getMessage());
        }
    }
}