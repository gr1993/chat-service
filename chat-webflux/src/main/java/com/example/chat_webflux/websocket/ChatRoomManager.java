package com.example.chat_webflux.websocket;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatRoomManager {
    // 채팅방 ID -> 해당 채팅방의 메시지 스트림
    private final ConcurrentHashMap<String, Sinks.Many<String>> roomSinks = new ConcurrentHashMap<>();

    public Sinks.Many<String> getRoomSink(String roomId) {
        return roomSinks.computeIfAbsent(roomId, k ->
                Sinks.many().multicast().onBackpressureBuffer()
        );
    }
}
