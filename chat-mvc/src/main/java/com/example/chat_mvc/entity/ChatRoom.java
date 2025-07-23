package com.example.chat_mvc.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoom {
    private Long id;
    private String name;
    private LocalDateTime createDt;

    private List<User> userList;
    private List<ChatMessage> messagesList;

    public ChatRoom(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createDt = LocalDateTime.now();
    }
}
