package com.example.chat_webflux.service;

import com.example.chat_webflux.dto.ChatRoomInfo;
import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.repository.ChatRoomRepository;
import com.example.chat_webflux.websocket.ChatRoomManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomManager chatRoomManager;

    @Test
    void getRoomList_성공() {
        // given
        List<ChatRoom> mockRoomList = List.of(
                new ChatRoom(1L, "park"),
                new ChatRoom(2L, "kang")
        );
        when(chatRoomRepository.findAll())
                .thenReturn(Flux.fromIterable(mockRoomList));

        // when
        List<ChatRoomInfo> roomList = chatRoomService.getRoomList().block();

        // then
        assertFalse(roomList.isEmpty());
        assertEquals(2, roomList.size());
    }

    @Test
    void createRoom_성공() {
        // given
        String roomName = "park";
        Sinks.Many<String> mockSink = mock(Sinks.Many.class);
        when(chatRoomManager.getChatServerSinks())
                .thenReturn(mockSink);

        // when
        chatRoomService.createRoom(roomName).block();

        // then
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(mockSink).tryEmitNext(anyString());
    }
}
