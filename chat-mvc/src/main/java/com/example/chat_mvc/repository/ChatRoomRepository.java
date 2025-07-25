package com.example.chat_mvc.repository;

import com.example.chat_mvc.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    void save(ChatRoom chatRoom);
    List<ChatRoom> findAll();
    Optional<ChatRoom> findById(Long roomId);
    default void update(ChatRoom chatRoom) {
        // do nothing
    }
}
