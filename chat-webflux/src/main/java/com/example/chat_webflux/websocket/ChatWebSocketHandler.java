package com.example.chat_webflux.websocket;

import com.example.chat_webflux.common.ChatSessionManager;
import com.example.chat_webflux.common.RoomUserSessionManager;
import com.example.chat_webflux.dto.WebSocketRoomUser;
import com.example.chat_webflux.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ChatRoomService chatRoomService;
    private final StompMessageRouter stompMessageRouter;
    private final ChatSessionManager chatSessionManager;
    private final RoomUserSessionManager roomUserSessionManager;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    /**
     * 웹소켓 연결 이벤트 처리
     * 해당 연결의 수명 주기 전체를 책임지는 하나의 리액티브 스트림을 정의하고 반환
     * 클라이언트로부터 들어오는 메시지(input)와 서버에서 클라이언트로 나가는 메시지(output)를 모두 하나의 파이프라인에서 처리하도록 설계
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 각 세션마다 메시지를 받아 발행할 Sinks를 생성
        // 이 Sinks는 오직 현재 연결된 클라이언트(세션)에게만 메시지를 보낸다.
        String sessionId = session.getId();
        Sinks.Many<String> sessionSink = chatSessionManager.createSessionSink(sessionId);

        // 1. 메시지 수신 스트림 (클라이언트 -> 서버)
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(message -> stompMessageRouter.handleStompMessage(sessionSink, session, message))
                .doFinally(signal -> {
                    // 커넥션 종료 시 로직
                    chatSessionManager.completeSessionSink(sessionId);
                    WebSocketRoomUser roomUser = roomUserSessionManager.removeUserSession(sessionId);
                    if (roomUser != null) {
                        // 해당 사용자 퇴장 처리
                        chatRoomService.exitRoom(roomUser.getRoomId(), roomUser.getUserId());
                    }
                    activeConnections.decrementAndGet();
                })
                .then();

        // 2. 메시지 송신 스트림 (서버 -> 클라이언트)
        Flux<WebSocketMessage> output = sessionSink.asFlux()
                .map(session::textMessage);

        // input과 output을 합쳐 하나의 리액티브 체인으로 생성
        return session.send(output).and(input);
    }
}