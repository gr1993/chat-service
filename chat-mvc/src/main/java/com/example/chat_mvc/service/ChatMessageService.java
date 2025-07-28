package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.entity.ChatMessage;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.MessageType;
import com.example.chat_mvc.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * 채팅방에서 사용자 메세지 처리
     */
    public void sendMessageToRoom(Long roomId, String userId, String message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 채팅방입니다."));

        long messageId = idGenerator.incrementAndGet();
        ChatMessage chatMessage = new ChatMessage(
                messageId,
                userId,
                message,
                LocalDateTime.now()
        );
        room.getMessagesQueue().add(chatMessage);

        // 채팅방에 새 메세지를 구독자들에게 알림
        broadcastUserMsg(roomId, chatMessage);
    }

    public void broadcastSystemMsg(ChatRoom room, String senderId, String message) {
        long messageId = idGenerator.incrementAndGet();
        ChatMessage chatMessage = new ChatMessage(
                messageId,
                senderId,
                message,
                LocalDateTime.now()
        );
        room.getMessagesQueue().add(chatMessage);
        broadcastMsg(room.getId(), chatMessage, true);
    }

    private void broadcastUserMsg(Long roomId, ChatMessage chatMessage) {
        broadcastMsg(roomId, chatMessage, false);
    }

    private void broadcastMsg(Long roomId, ChatMessage chatMessage, boolean isSystem) {
        ChatMessageInfo messageInfo = new ChatMessageInfo();
        messageInfo.setMessageId(chatMessage.getId());
        messageInfo.setSenderId(chatMessage.getSenderId());
        messageInfo.setMessage(chatMessage.getMessage());
        messageInfo.setSendDt(chatMessage.getSendDt().toString());
        messageInfo.setType(isSystem ? MessageType.system.name() : MessageType.user.name());
        messagingTemplate.convertAndSend("/topic/message/" + roomId, messageInfo);
    }
}
