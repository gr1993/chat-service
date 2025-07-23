package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.repository.ChatRoomRepository;
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
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatRoomWebSocketTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @MockitoBean
    private ChatRoomRepository chatRoomRepository;

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
        StompSession session = connectWebSocket();
        BlockingQueue<ChatRoomInfo> blockingQueue = new LinkedBlockingQueue<>();

        session.subscribe("/topic/rooms", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatRoomInfo.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((ChatRoomInfo) payload);
            }
        });

        // when
        String roomName = "park";
        chatRoomService.createRoom(roomName);

        // then
        ChatRoomInfo receivedMessage = blockingQueue.poll(5, TimeUnit.SECONDS);
        log.info("받은 메세지 객체 : {}", receivedMessage);
        assertNotNull(receivedMessage);
        assertEquals(roomName, receivedMessage.getRoomName());
    }

    private StompSession connectWebSocket() throws ExecutionException, InterruptedException, TimeoutException {
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
    }
}
