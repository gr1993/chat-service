package com.example.chat_mvc.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class ChatRoom {
    private Long id;
    private String name;
    private LocalDateTime createDt;

    /**
     * 성능 측정 시 채팅방 인원 확인용
     * 퇴장할 경우 중간에 요소가 삭제될 수 있어 Map 사용
     */
    private Map<String, User> userMap = new ConcurrentHashMap<>();

    /**
     * 다중 접속 시 밀려오는 메세지를 순차 저장하기 위해 Queue 사용
     */
    private Queue<ChatMessage> messagesQueue = new ConcurrentLinkedQueue<>();

    public ChatRoom(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createDt = LocalDateTime.now();
    }
}
