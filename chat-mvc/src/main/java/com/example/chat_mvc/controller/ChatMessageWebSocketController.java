package com.example.chat_mvc.controller;

import com.example.chat_mvc.dto.SendMessageInfo;
import com.example.chat_mvc.service.ChatMessageService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageWebSocketController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/messages")
    @Timed("websocket_message_seconds")
    public void sendMessage(SendMessageInfo messageInfo) throws Exception {
        chatMessageService.sendMessageToRoom(
                messageInfo.getRoomId(),
                messageInfo.getUserId(),
                messageInfo.getMessage()
        );
    }
}
