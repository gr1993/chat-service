package com.example.chat_mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageInfo {
    private Long roomId;
    private String userId;
    private String message;
}
