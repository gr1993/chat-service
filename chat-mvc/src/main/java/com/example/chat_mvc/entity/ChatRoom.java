package com.example.chat_mvc.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class ChatRoom {
    private Long id;
    private String name;
    private LocalDateTime createDt;

    private Queue<User> userQueue;  // 성능 측정 시 채팅방 인원 확인용
    private Queue<ChatMessage> messagesQueue;

    public ChatRoom(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createDt = LocalDateTime.now();

        userQueue = new ConcurrentLinkedQueue<>();
        messagesQueue = new ConcurrentLinkedQueue<>();
    }
}
