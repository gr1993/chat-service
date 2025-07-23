package com.example.chat_mvc.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private String senderId;
    private String message;
    private LocalDateTime sendDt;
}
