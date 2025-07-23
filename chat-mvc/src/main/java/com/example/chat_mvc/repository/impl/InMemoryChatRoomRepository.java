package com.example.chat_mvc.repository.impl;

import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.repository.ChatRoomRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryChatRoomRepository implements ChatRoomRepository {

    /**
     * 읽기가 많고 쓰기가 적을 것으로 예상되어 CopyOnWriteArrayList 사용
     */
    private final List<ChatRoom> chatRoomList = new CopyOnWriteArrayList<>();

    @Override
    public void save(ChatRoom chatRoom) {
        chatRoomList.add(chatRoom);
    }

    @Override
    public List<ChatRoom> findAll() {
        return new ArrayList<>(chatRoomList);
    }
}
