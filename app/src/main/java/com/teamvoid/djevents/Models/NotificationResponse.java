package com.teamvoid.djevents.Models;

public class NotificationResponse {
    String messageId;

    public NotificationResponse(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
