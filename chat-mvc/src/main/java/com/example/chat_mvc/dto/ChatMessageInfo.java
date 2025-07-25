package com.example.chat_mvc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageInfo {
    private Long messageId;
    private String senderId;
    private String message;
    private String sendDt;
    private String type;
}
