package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void sendMessageToRoom_성공() {
        // given
        Long roomId = 1L;
        String userId = "lim";
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(new ChatRoom(roomId, "park")));

        // when
        chatMessageService.sendMessageToRoom(roomId, userId, "메세지 전송~");

        // then
        verify(messagingTemplate).convertAndSend(eq("/topic/message/" + roomId), any(ChatMessageInfo.class));
    }
}
