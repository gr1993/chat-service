package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.ChatRoomRepository;
import com.example.chat_mvc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @Test
    void getRoomList_성공() {
        // given
        List<ChatRoom> mockRoomList = List.of(
                new ChatRoom(1L, "park"),
                new ChatRoom(2L, "kang")
        );
        when(chatRoomRepository.findAll())
                .thenReturn(mockRoomList);

        // when
        List<ChatRoomInfo> roomList = chatRoomService.getRoomList();

        // then
        assertFalse(roomList.isEmpty());
        assertEquals(2, roomList.size());
    }

    @Test
    void createRoom_성공() {
        // given
        String roomName = "park";

        // when
        chatRoomService.createRoom(roomName);

        // then
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/rooms"), any(ChatRoomInfo.class));
    }

    @Test
    void enterRoom_채팅방_없음_실패() {
        // given
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
            IllegalArgumentException.class,
            () -> chatRoomService.enterRoom(1L, "park")
        );
    }

    @Test
    void enterRoom_사용자_없음_실패() {
        // given
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(new ChatRoom(1L, "park")));
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> chatRoomService.enterRoom(1L, "park")
        );
    }
    
    @Test
    void enterRoom_성공() {
        // given
        Long roomId = 1L;
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(new ChatRoom(roomId, "park")));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(new User("kang")));

        // when
        chatRoomService.enterRoom(1L, "kang");

        // then
        verify(chatRoomRepository).update(any(ChatRoom.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/message/" + roomId), any(ChatMessageInfo.class));
    }
}
