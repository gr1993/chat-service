package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;


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
    }
}
