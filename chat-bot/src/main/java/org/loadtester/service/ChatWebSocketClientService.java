package org.loadtester.service;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class ChatWebSocketClientService {

    private final WebSocketStompClient stompClient;
    private final String url;

    public ChatWebSocketClientService(String url) {
        this.url = url;

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public StompSession connectWebSocket(Consumer<StompSession> onConnect) {
        try {
            return stompClient.connectAsync(
                            url,
                            new StompSessionHandlerAdapter() {
                                @Override
                                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                                    onConnect.accept(session);
                                }

                                @Override
                                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                                    System.err.println("예외 발생: " + exception.getMessage());
                                    //exception.printStackTrace();
                                }
                            }
                    )
                    .get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("웹소켓 연결 실패", e);
        }
    }

    public static void subscribeStomp(StompSession stompSession, String destination, Class<?> clazz) {
        stompSession.subscribe(destination, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return clazz;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("메시지 수신: " + clazz.cast(payload));
            }
        });
    }
}
