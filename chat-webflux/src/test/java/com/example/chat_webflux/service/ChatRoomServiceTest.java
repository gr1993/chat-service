package com.example.chat_webflux.service;

import com.example.chat_webflux.dto.ChatRoomInfo;
import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.entity.User;
import com.example.chat_webflux.repository.ChatRoomRepository;
import com.example.chat_webflux.repository.UserRepository;
import com.example.chat_webflux.common.ChatRoomManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

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

    @Test
    void enterRoom_채팅방_없음_실패() {
        // given
        when(chatRoomRepository.findById(any()))
                .thenReturn(Mono.empty());
        when(userRepository.findById(any()))
                .thenReturn(Mono.empty());

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> chatRoomService.enterRoom(1L, "park").block()
        );
    }

    @Test
    void enterRoom_사용자_없음_실패() {
        // given
        ChatRoom chatRoom = new ChatRoom(1L, "park");
        when(chatRoomRepository.findById(any()))
                .thenReturn(Mono.just(chatRoom));
        when(userRepository.findById(any()))
                .thenReturn(Mono.empty());

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> chatRoomService.enterRoom(chatRoom.getId(), chatRoom.getName()).block()
        );
    }

    @Test
    void enterRoom_성공() {
        // given
        ChatRoom chatRoom = new ChatRoom(1L, "park");
        User user = new User("kang");
        when(chatRoomRepository.findById(any()))
                .thenReturn(Mono.just(chatRoom));
        when(userRepository.findById(any()))
                .thenReturn(Mono.just(user));
        when(chatRoomRepository.update(any()))
                .thenReturn(Mono.empty());
        when(chatMessageService.broadcastSystemMsg(any(), any(), any()))
                .thenReturn(Mono.empty());

        // when
        chatRoomService.enterRoom(chatRoom.getId(), user.getId()).block();

        // then
        verify(chatRoomRepository).update(any(ChatRoom.class));
        verify(chatMessageService).broadcastSystemMsg(any(), any(), any());
    }

    @Test
    void exitRoom_성공() {
        // given
        ChatRoom chatRoom = new ChatRoom(1L, "park");
        User user = new User("kang");
        when(chatRoomRepository.findById(any()))
                .thenReturn(Mono.just(chatRoom));
        when(userRepository.findById(any()))
                .thenReturn(Mono.just(user));
        when(chatRoomRepository.update(any()))
                .thenReturn(Mono.empty());
        when(chatMessageService.broadcastSystemMsg(any(), any(), any()))
                .thenReturn(Mono.empty());

        // when
        chatRoomService.exitRoom(chatRoom.getId(), user.getId()).block();

        // then
        verify(chatRoomRepository).update(any(ChatRoom.class));
        verify(chatMessageService).broadcastSystemMsg(any(), any(), any());
    }
}
