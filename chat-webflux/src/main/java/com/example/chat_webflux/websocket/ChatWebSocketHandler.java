package com.example.chat_webflux.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    private final StompMessageRouter stompMessageRouter;


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
                .doOnNext(message -> stompMessageRouter.handleStompMessage(sessionSink, session, message))
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
}