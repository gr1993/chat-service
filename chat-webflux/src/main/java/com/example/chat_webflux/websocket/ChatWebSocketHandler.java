package com.example.chat_webflux.websocket;

import com.example.chat_webflux.dto.ChatMessageInfo;
import com.example.chat_webflux.dto.SendMessageInfo;
import com.example.chat_webflux.entity.MessageType;
import com.example.chat_webflux.util.StompFrameParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    // 채팅방 ID -> 해당 채팅방의 메시지 스트림을 관리하는 맵
    private final ConcurrentHashMap<String, Sinks.Many<String>> roomSinks = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 웹소켓 연결 이벤트 처리
     * 해당 연결의 수명 주기 전체를 책임지는 하나의 리액티브 스트림을 정의하고 반환
     * 클라이언트로부터 들어오는 메시지(input)와 서버에서 클라이언트로 나가는 메시지(output)를 모두 하나의 파이프라인에서 처리하도록 설계
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 각 세션마다 메시지를 받아 발행할 Sinks를 생성
        // 이 Sinks는 오직 현재 연결된 클라이언트(세션)에게만 메시지를 보낸다.
        Sinks.Many<String> sessionSink = Sinks.many().unicast().onBackpressureBuffer();

        // 1. 메시지 수신 스트림 (클라이언트 -> 서버)
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> handleStompMessage(sessionSink, session, message))
                .doFinally(signal -> {
                    // 세션이 종료되면 해당 세션의 구독을 해지
                    sessionSink.tryEmitComplete();
                })
                .then();

        // 2. 메시지 송신 스트림 (서버 -> 클라이언트)
        Flux<WebSocketMessage> output = sessionSink.asFlux()
                .map(session::textMessage);

        // input과 output을 합쳐 하나의 리액티브 체인으로 생성
        return session.send(output).and(input);
    }

    private void handleStompMessage(Sinks.Many<String> sessionSink, WebSocketSession session, String stompMessage) {
        String command = StompFrameParser.getCommand(stompMessage);

        switch (command) {
            case "CONNECT":
                handleConnect(sessionSink);
                break;
            case "SEND":
                try {
                    handleSend(stompMessage);
                } catch (Exception ex) {
                    return;
                }
                break;
            case "SUBSCRIBE":
                handleSubscribe(sessionSink, stompMessage);
                break;
            case "DISCONNECT":
                // 연결 해제 처리 로직
                break;
            case "HEARTBEAT":
                // 하트비트 처리 로직
                break;
            default:
                // 알 수 없는 커맨드 처리
                break;
        }
    }

    private void handleConnect(Sinks.Many<String> sessionSink) {
        String connectedFrame = "CONNECTED\n" +
                "version:1.2\n" +
                "heart-beat:0,0\n" +
                "\n\0";

        sessionSink.tryEmitNext(connectedFrame);
    }

    private void handleSend(String stompMessage) throws Exception {
        String destination = StompFrameParser.getHeader(stompMessage, "destination");
        String jsonBody = StompFrameParser.getBody(stompMessage);

        // 메세지 전송
        if (destination != null && destination.startsWith("/api/messages") && jsonBody != null) {
            SendMessageInfo sendMessageInfo = objectMapper.readValue(jsonBody, SendMessageInfo.class);
            Long roomId = sendMessageInfo.getRoomId();

            // 해당 채팅방의 Sinks.Many를 가져오거나 없으면 새로 생성
            Sinks.Many<String> roomSink = roomSinks.computeIfAbsent(roomId.toString(), k ->
                    Sinks.many().multicast().onBackpressureBuffer()
            );

            // 메시지 발행 (해당 방의 구독자들에게만 전달)
            ChatMessageInfo chatMessageInfo = new ChatMessageInfo();
            chatMessageInfo.setMessageId(1L);
            chatMessageInfo.setType(MessageType.user.name());
            chatMessageInfo.setSenderId("lim");
            chatMessageInfo.setSendDt("2025-08-13T15:37:40");
            chatMessageInfo.setMessage(sendMessageInfo.getMessage());
            roomSink.tryEmitNext(objectMapper.writeValueAsString(chatMessageInfo));
        }
    }

    private void handleSubscribe(Sinks.Many<String> sessionSink, String stompMessage) {
        String destination = StompFrameParser.getHeader(stompMessage, "destination");
        String subscriptionId = StompFrameParser.getHeader(stompMessage, "id"); // 구독 ID 추출

        if (destination != null && destination.startsWith("/topic/message/") && subscriptionId != null) {
            String roomId = destination.substring("/topic/message/".length());

            // 해당 채팅방의 Sinks.Many를 가져오거나 없으면 새로 생성
            Sinks.Many<String> roomSink = roomSinks.computeIfAbsent(roomId, k ->
                    Sinks.many().multicast().onBackpressureBuffer()
            );

            // 이 Flux를 현재 세션의 sessionSink에 구독
            roomSink.asFlux()
                    .map(json -> {
                        String stompFrame = StompFrameParser.createStompMessageFrame(destination, subscriptionId, json);
                        return stompFrame; // 이제 STOMP 프레임이 된 문자열을 sessionSink에 발행
                    })
                    .subscribe(
                            // 이 메시지는 handle()의 output 스트림을 타고 클라이언트에게 전달
                            message -> sessionSink.tryEmitNext(message),
                            error -> {},
                            () -> {}
                    );
        }
    }
}