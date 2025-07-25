package com.example.chat_mvc.repository;

import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.impl.InMemoryChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void setUp() {
        if (chatRoomRepository instanceof InMemoryChatRoomRepository repo) {
            repo.clear();
        }
    }

    @Test
    void save_성공() {
        // given
        ChatRoom chatRoom = new ChatRoom(1L, "park");
        chatRoom.setCreateDt(LocalDateTime.now());

        // when
        chatRoomRepository.save(chatRoom);

        // then
        List<ChatRoom> roomList = chatRoomRepository.findAll();
        assertFalse(roomList.isEmpty());
        ChatRoom searchRoom = roomList.get(0);
        assertEquals(chatRoom.getId(), searchRoom.getId());
        assertEquals(chatRoom.getName(), searchRoom.getName());
        assertEquals(chatRoom.getCreateDt(), searchRoom.getCreateDt());
    }

    @Test
    void findAll_성공() {
        // given
        chatRoomRepository.save(new ChatRoom(1L, "park"));
        chatRoomRepository.save(new ChatRoom(2L, "kang"));

        // when
        List<ChatRoom> roomList = chatRoomRepository.findAll();

        // then
        assertFalse(roomList.isEmpty());
        assertEquals(2, roomList.size());
    }

    @Test
    void findById_성공() {
        // given
        chatRoomRepository.save(new ChatRoom(1L, "park"));
        chatRoomRepository.save(new ChatRoom(2L, "kang"));

        // when
        Optional<ChatRoom> roomOptional = chatRoomRepository.findById(2L);

        // then
        assertTrue(roomOptional.isPresent());
        ChatRoom room = roomOptional.get();
        assertEquals("kang", room.getName());
    }

    @Test
    void update_성공() {
        // given
        chatRoomRepository.save(new ChatRoom(1L, "park"));
        ChatRoom updateRoom = chatRoomRepository.findById(1L).orElse(null);

        // when
        User user = new User("kang");
        updateRoom.getUserQueue().add(user);
        chatRoomRepository.update(updateRoom);

        // then
        Optional<ChatRoom> roomOptional = chatRoomRepository.findById(1L);
        assertTrue(roomOptional.isPresent());
        ChatRoom room = roomOptional.get();
        assertEquals(1, room.getUserQueue().size());
    }
}
