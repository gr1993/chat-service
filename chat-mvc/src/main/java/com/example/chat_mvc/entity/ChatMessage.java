package com.example.chat_mvc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private String senderId;
    private String message;
    private LocalDateTime sendDt;
}
