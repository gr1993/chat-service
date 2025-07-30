package org.loadtester.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageInfo {
    private Long messageId;
    private String senderId;
    private String message;
    private String sendDt;
    private String type;
}