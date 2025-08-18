package com.example.chat_webflux.service;

import com.example.chat_webflux.dto.ChatMessageInfo;
import com.example.chat_webflux.entity.ChatMessage;
import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.entity.MessageType;
import com.example.chat_webflux.repository.ChatRoomRepository;
import com.example.chat_webflux.websocket.ChatRoomManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomManager chatRoomManager;

    private final AtomicLong idGenerator = new AtomicLong(0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Void> sendMessageToRoom(Long roomId, String userId, String message) {
        return chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("존재하지 않은 채팅방입니다.")))
                .flatMap(room -> {
                    ChatMessage chatMessage = createMsg(room, userId, message);

                    // 채팅방에 새 메세지를 구독자들에게 알림
                    return broadcastMsg(roomId, chatMessage, false);
                });
    }

    public Mono<Void> broadcastSystemMsg(ChatRoom room, String userId, String message) {
        ChatMessage chatMessage = createMsg(room, userId, message);

        // 채팅방에 새 메세지를 구독자들에게 알림
        return broadcastMsg(room.getId(), chatMessage, true);
    }

    private ChatMessage createMsg(ChatRoom room, String userId, String message) {
        long messageId = idGenerator.incrementAndGet();

        ChatMessage chatMessage = new ChatMessage(
                messageId,
                userId,
                message,
                LocalDateTime.now()
        );
        room.getMessagesQueue().add(chatMessage);
        return chatMessage;
    }

    private Mono<Void> broadcastMsg(Long roomId, ChatMessage chatMessage, boolean isSystem) {
        try {
            ChatMessageInfo messageInfo = new ChatMessageInfo();
            messageInfo.setMessageId(chatMessage.getId());
            messageInfo.setSenderId(chatMessage.getSenderId());
            messageInfo.setMessage(chatMessage.getMessage());
            messageInfo.setSendDt(chatMessage.getSendDt().toString());
            messageInfo.setType(isSystem ? MessageType.system.name() : MessageType.user.name());

            Sinks.Many<String> roomSink = chatRoomManager.getRoomSink(roomId.toString());
            roomSink.tryEmitNext(objectMapper.writeValueAsString(messageInfo));

            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
