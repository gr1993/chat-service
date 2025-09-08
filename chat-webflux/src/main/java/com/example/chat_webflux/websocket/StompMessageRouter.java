package com.example.chat_webflux.websocket;

import com.example.chat_webflux.dto.SendMessageInfo;
import com.example.chat_webflux.service.ChatMessageService;
import com.example.chat_webflux.service.SubscriptionService;
import com.example.chat_webflux.util.StompFrameParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class StompMessageRouter {

    private final ChatMessageService chatMessageService;
    private final SubscriptionService subscriptionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleStompMessage(Sinks.Many<String> sessionSink, WebSocketSession session, String stompMessage) {
        String command = StompFrameParser.getCommand(stompMessage);

        switch (command) {
            case "CONNECT":
                handleConnect(sessionSink, session.getId());
                break;
            case "SEND":
                try {
                    handleSend(stompMessage);
                } catch (Exception ex) {
                    return;
                }
                break;
            case "SUBSCRIBE":
                handleSubscribe(sessionSink, session, stompMessage);
                break;
            case "UNSUBSCRIBE":
                handleUnsubscribe(session, stompMessage);
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

    private void handleConnect(Sinks.Many<String> sessionSink, String sessionId) {
        String connectedFrame = "CONNECTED\n" +
                "version:1.2\n" +
                "heart-beat:0,0\n" +
                "session:" + sessionId + "\n" +
                "\n\0";

        sessionSink.tryEmitNext(connectedFrame);
    }

    private void handleSend(String stompMessage) throws Exception {
        String destination = StompFrameParser.getHeader(stompMessage, "destination");
        String jsonBody = StompFrameParser.getBody(stompMessage);

        // 메세지 전송
        if (destination != null && destination.startsWith("/api/messages") && jsonBody != null) {
            SendMessageInfo sendMessageInfo = objectMapper.readValue(jsonBody, SendMessageInfo.class);

            // 메시지 발행 (해당 방의 구독자들에게만 전달)
            chatMessageService.sendMessageToRoom(sendMessageInfo.getRoomId(), sendMessageInfo.getUserId(), sendMessageInfo.getMessage())
                    .subscribe();
        }
    }

    private void handleSubscribe(Sinks.Many<String> sessionSink, WebSocketSession session, String stompMessage) {
        String destination = StompFrameParser.getHeader(stompMessage, "destination");
        String subscriptionId = StompFrameParser.getHeader(stompMessage, "id"); // 구독 ID 추출
        String sessionId = session.getId();

        if (destination == null || subscriptionId == null) {
            return;
        }

        // 채팅방 메시지 구독
        if (destination.startsWith("/topic/message/")) {
            subscriptionService.subscribeRoomMessage(sessionSink, destination, sessionId, subscriptionId);
        }
        // 채팅방 생성 구독
        else if (destination.startsWith("/topic/rooms")) {
            subscriptionService.subscribeRoomCreate(sessionSink, destination, sessionId, subscriptionId);
        }
    }

    private void handleUnsubscribe(WebSocketSession session, String stompMessage) {
        String subscriptionId = StompFrameParser.getHeader(stompMessage, "id");
        if (subscriptionId != null) {
            String sessionId = session.getId();
            subscriptionService.unSubscribe(sessionId, subscriptionId);
        }
    }
}
