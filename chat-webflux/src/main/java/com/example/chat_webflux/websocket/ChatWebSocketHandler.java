package com.example.chat_webflux.websocket;

import com.example.chat_webflux.util.StompFrameParser;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<String> chatMessageSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<String> chatMessages = chatMessageSink.asFlux();

    /**
     * 웹소켓 연결 이벤트 처리
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 클라이언트로부터 들어오는 메시지 스트림
        Mono<Void> input = session.receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .doOnNext(message -> handleStompMessage(session, message))
                .then();

        // 클라이언트에게 보내는 메시지 스트림
        Mono<Void> output = session.send(chatMessages
                .map(json -> {
                    String stompFrame = StompFrameParser.createStompMessageFrame("/topic/chat", json);
                    return session.textMessage(stompFrame);
                })
        );

        return Mono.zip(input, output).then();
    }

    private void handleStompMessage(WebSocketSession session, String stompMessage) {
        String command = StompFrameParser.getCommand(stompMessage);

        switch (command) {
            case "CONNECT":
                handleConnect(session);
                break;
            case "SEND":
                handleSend(stompMessage);
                break;
            case "SUBSCRIBE":
                // 구독 처리 로직
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

    private void handleConnect(WebSocketSession session) {
        String connectedFrame = "CONNECTED\n" +
                "version:1.2\n" +
                "heart-beat:0,0\n" +
                "\n\0";
        session.send(Mono.just(session.textMessage(connectedFrame))).subscribe();
    }

    private void handleSend(String stompMessage) {
        String jsonBody = StompFrameParser.getBody(stompMessage);
        if (jsonBody != null) {
            chatMessageSink.tryEmitNext(jsonBody);
        }
    }
}