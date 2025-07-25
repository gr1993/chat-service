package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.MessageType;
import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.ChatRoomRepository;
import com.example.chat_mvc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatRoomWebSocketTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @MockitoBean
    private ChatRoomRepository chatRoomRepository;

    @MockitoBean
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String URL;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        URL = "ws://localhost:" + port + "/ws";
    }

    @Test
    void 서버에서_채팅방_생성_알림_받기() throws Exception {
        // given
        BlockingQueue<ChatRoomInfo> blockingQueue = getWebSocketQueue("/topic/rooms", ChatRoomInfo.class);

        // when
        String roomName = "park";
        chatRoomService.createRoom(roomName);

        // then
        ChatRoomInfo chatRoomInfo = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", chatRoomInfo);
        assertNotNull(chatRoomInfo);
        assertEquals(roomName, chatRoomInfo.getRoomName());
    }

    @Test
    void 서버에서_채팅방_입장_알림_받기() throws Exception {
        // given
        Long roomId = 1L;
        String userId = "kang";
        when(chatRoomRepository.findById(any()))
                .thenReturn(Optional.of(new ChatRoom(roomId, "park")));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(new User(userId)));

        BlockingQueue<ChatMessageInfo> blockingQueue = getWebSocketQueue("/topic/message/" + roomId, ChatMessageInfo.class);

        // when
        chatRoomService.enterRoom(roomId, userId);

        // then
        ChatMessageInfo chatMessageInfo = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", chatMessageInfo);
        assertNotNull(chatMessageInfo);
        assertNotNull(chatMessageInfo.getMessageId());
        assertEquals(userId, chatMessageInfo.getSenderId());
        assertTrue(StringUtils.hasText(chatMessageInfo.getMessage()));
        assertTrue(StringUtils.hasText(chatMessageInfo.getSendDt()));
        assertEquals(MessageType.system.name(), chatMessageInfo.getType());
    }


    private StompSession connectWebSocket() {
        try {
            return stompClient.connectAsync(
                            URL,
                            new StompSessionHandlerAdapter() {
                                @Override
                                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                                    log.info("웹소켓 연결 성공");
                                }
                            }
                    )
                    .get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("웹소켓 연결 실패", e);
        }
    }

    private <T> BlockingQueue<T> getWebSocketQueue (String destination, Class<T> clazz) {
        BlockingQueue<T> blockingQueue = new LinkedBlockingQueue<>();

        StompSession session = connectWebSocket();
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return clazz;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add(clazz.cast(payload));
            }
        });

        return blockingQueue;
    }
}
