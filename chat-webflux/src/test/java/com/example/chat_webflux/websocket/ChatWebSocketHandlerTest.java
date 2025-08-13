package com.example.chat_webflux.websocket;


import com.example.chat_webflux.dto.ChatMessageInfo;
import com.example.chat_webflux.dto.SendMessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 웹소켓용 컨트롤러 통합 테스트 클래스
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatWebSocketHandlerTest {
    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String URL;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        URL = "ws://localhost:" + port + "/ws";
    }

    /**
     * 메세지 전송 및 구독까지 통합 테스트
     */
    @Test
    void sendMessage_성공() throws Exception {
        // given
        Long roomId = 1L;
        String userId = "lim";
        String message = "안녕하세요~";
        StompSession session = connectWebSocket();
        BlockingQueue<ChatMessageInfo> blockingQueue = getWebSocketQueue(
                session,
                "/topic/message/" + roomId,
                ChatMessageInfo.class
        );

        // when
        SendMessageInfo messageInfo = new SendMessageInfo(
                roomId,
                userId,
                message
        );
        session.send("/api/messages", messageInfo);

        // then
        ChatMessageInfo chatMessageInfo = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", chatMessageInfo);
        assertNotNull(chatMessageInfo);
        assertNotNull(chatMessageInfo.getMessageId());
        assertEquals(userId, chatMessageInfo.getSenderId());
        assertTrue(StringUtils.hasText(chatMessageInfo.getMessage()));
        assertEquals(message, chatMessageInfo.getMessage());
        assertTrue(StringUtils.hasText(chatMessageInfo.getSendDt()));
        //assertEquals(MessageType.user.name(), chatMessageInfo.getType());
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
}
