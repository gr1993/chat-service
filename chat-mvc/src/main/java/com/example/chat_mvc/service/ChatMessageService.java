package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.MessageType;
import com.example.chat_mvc.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final AtomicLong idGenerator = new AtomicLong(0);

    public void broadcastSystemMsg(ChatRoom room, String senderId, String message) {
        broadcastMsg(room, senderId, message, true);
    }

    public void broadcastUserMsg(ChatRoom room, String senderId, String message) {
        broadcastMsg(room, senderId, message, false);
    }

    private void broadcastMsg(ChatRoom room, String senderId, String message, boolean isSystem) {
        long messageId = idGenerator.incrementAndGet();

        ChatMessageInfo messageInfo = new ChatMessageInfo();
        messageInfo.setMessageId(messageId);
        messageInfo.setSenderId(senderId);
        messageInfo.setMessage(message);
        messageInfo.setSendDt(LocalDateTime.now().toString());
        messageInfo.setType(isSystem ? MessageType.system.name() : MessageType.user.name());
        messagingTemplate.convertAndSend("/topic/message/" + room.getId(), messageInfo);
    }
}
