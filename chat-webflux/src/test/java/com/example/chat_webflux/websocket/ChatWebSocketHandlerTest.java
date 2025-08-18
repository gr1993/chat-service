package com.example.chat_webflux.websocket;


import com.example.chat_webflux.dto.ChatMessageInfo;
import com.example.chat_webflux.dto.ChatRoomInfo;
import com.example.chat_webflux.dto.SendMessageInfo;
import com.example.chat_webflux.entity.MessageType;
import com.example.chat_webflux.repository.UserRepository;
import com.example.chat_webflux.service.ChatRoomService;
import com.example.chat_webflux.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.util.StringUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 웹소켓용 컨트롤러 통합 테스트 클래스
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatWebSocketHandlerTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession session;
    private String URL;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        URL = "ws://localhost:" + port + "/ws";

        session = connectWebSocket();
    }

    /**
     * 메세지 전송 및 구독까지 통합 테스트
     */
    @Test
    void sendMessage_성공() throws Exception {
        roomWebSocketSubscribeTest(true, (roomId, userId) -> {
            String message = "안녕하세요~";
            SendMessageInfo messageInfo = new SendMessageInfo(
                    roomId,
                    userId,
                    message
            );
            session.send("/api/messages", messageInfo);
            return message;
        });
    }

    /**
     * 채팅방 생성 구독 통합 테스트
     */
    @Test
    void createRoom_성공() throws Exception {
        // given
        BlockingQueue<ChatRoomInfo> blockingQueue = getWebSocketQueue(
                session,
                "/topic/rooms",
                ChatRoomInfo.class
        );

        // when
        String roomName = "park";
        chatRoomService.createRoom(roomName).block();

        // then
        ChatRoomInfo chatRoomInfo = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", chatRoomInfo);
        assertNotNull(chatRoomInfo);
        assertEquals(roomName, chatRoomInfo.getRoomName());
    }

    /**
     * 채팅방 입장 메시지 구독 통합 테스트
     */
    @Test
    void enterRoom_성공() throws Exception {
        roomWebSocketSubscribeTest(false, (roomId, userId) -> {
            chatRoomService.enterRoom(roomId, userId).block();
            return null;
        });
    }

    /**
     * 채팅방 퇴장 메시지 구독 통합 테스트
     */
    @Test
    void exitRoom_성공() throws Exception {
        roomWebSocketSubscribeTest(false, (roomId, userId) -> {
            chatRoomService.exitRoom(roomId, userId).block();
            return null;
        });
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

    private <T> BlockingQueue<T> getWebSocketQueue(StompSession session, String destination, Class<T> clazz) {
        BlockingQueue<T> blockingQueue = new LinkedBlockingQueue<>();

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

    private void roomWebSocketSubscribeTest(boolean isUserMsg, BiFunction<Long, String, String> function) throws Exception {
        // given
        String roomName = "park";
        chatRoomService.createRoom(roomName).block();
        List<ChatRoomInfo> roomInfoList = chatRoomService.getRoomList().block();

        Long roomId = roomInfoList.get(0).getRoomId();
        String userId = "lim";
        if (Boolean.FALSE.equals(userRepository.existsById(userId).block())) {
            userService.entryUser(userId).block();
        }

        BlockingQueue<ChatMessageInfo> blockingQueue = getWebSocketQueue(
                session,
                "/topic/message/" + roomId,
                ChatMessageInfo.class
        );

        // when
        String message = function.apply(roomId, userId);

        // then
        ChatMessageInfo chatMessageInfo = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", chatMessageInfo);
        assertNotNull(chatMessageInfo);
        assertNotNull(chatMessageInfo.getMessageId());
        assertEquals(userId, chatMessageInfo.getSenderId());
        assertTrue(StringUtils.hasText(chatMessageInfo.getMessage()));
        if (StringUtils.hasText(message)) {
            assertEquals(message, chatMessageInfo.getMessage());
        }
        assertTrue(StringUtils.hasText(chatMessageInfo.getSendDt()));
        assertEquals((isUserMsg ? MessageType.user.name() : MessageType.system.name()), chatMessageInfo.getType());
    }

    /**
     * Sink 정리
     */
    @AfterEach
    void tearDown() {
        if (session != null) {
            session.disconnect();
        }
    }
}
