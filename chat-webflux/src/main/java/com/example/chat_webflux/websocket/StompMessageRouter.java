package com.example.chat_webflux.websocket;

import com.example.chat_webflux.dto.ChatMessageInfo;
import com.example.chat_webflux.dto.SendMessageInfo;
import com.example.chat_webflux.entity.MessageType;
import com.example.chat_webflux.service.SubscriptionService;
import com.example.chat_webflux.util.StompFrameParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class StompMessageRouter {

    private final ChatRoomManager chatRoomManager;
    private final SubscriptionService subscriptionService;

    private final ConcurrentHashMap<String, Disposable> subscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void handleStompMessage(Sinks.Many<String> sessionSink, WebSocketSession session, String stompMessage) {
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
            Sinks.Many<String> roomSink = chatRoomManager.getRoomSink(roomId.toString());

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

    private void handleSubscribe(Sinks.Many<String> sessionSink, WebSocketSession session, String stompMessage) {
        String destination = StompFrameParser.getHeader(stompMessage, "destination");
        String subscriptionId = StompFrameParser.getHeader(stompMessage, "id"); // 구독 ID 추출
        String sessionId = session.getId();

        if (destination == null || subscriptionId == null) {
            return;
        }

        // 채팅방 메시지 구독
        Disposable disposable = null;
        if (destination.startsWith("/topic/message/")) {
            disposable = subscriptionService.subscribeRoomMessage(sessionSink, destination, subscriptionId);
        }
        // 채팅방 생성 구독
        else if (destination.startsWith("/topic/rooms")) {
            disposable = subscriptionService.subscribeRoomCreate(sessionSink, destination, subscriptionId);
        }

        // 구독 취소를 위해 구독 정보 저장
        if (disposable != null) {
            String key = sessionId + ":" + subscriptionId;
            subscriptions.put(key, disposable);
        }
    }

    private void handleUnsubscribe(WebSocketSession session, String stompMessage) {
        String subscriptionId = StompFrameParser.getHeader(stompMessage, "id");
        if (subscriptionId != null) {
            String sessionId = session.getId();
            String key = sessionId + ":" + subscriptionId;
            Disposable disposable = subscriptions.remove(key); // 맵에서 제거
            if (disposable != null) {
                disposable.dispose(); // 구독 취소
            }
        }
    }
}
