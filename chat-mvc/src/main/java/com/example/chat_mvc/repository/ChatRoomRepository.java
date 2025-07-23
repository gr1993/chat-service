package com.example.chat_mvc.repository;

import com.example.chat_mvc.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository {
    void save(ChatRoom chatRoom);
    List<ChatRoom> findAll();
}
