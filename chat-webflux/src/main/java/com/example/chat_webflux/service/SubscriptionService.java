package com.example.chat_webflux.service;

import com.example.chat_webflux.util.StompFrameParser;
import com.example.chat_webflux.websocket.ChatRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final ChatRoomManager chatRoomManager;

    /**
     * 채팅방 메시지 구독
     */
    public void subscribeRoomMessage(Sinks.Many<String> sessionSink, String destination, String subscriptionId) {
        String roomId = destination.substring("/topic/message/".length());

        // 해당 채팅방의 Sinks.Many를 가져오거나 없으면 새로 생성
        Sinks.Many<String> roomSink = chatRoomManager.getRoomSink(roomId.toString());

        // 이 Flux를 현재 세션의 sessionSink에 구독
        roomSink.asFlux()
                .map(json -> {
                    return StompFrameParser.createStompMessageFrame(destination, subscriptionId, json); // 이제 STOMP 프레임이 된 문자열을 sessionSink에 발행
                })
                .subscribe(
                        // 이 메시지는 handle()의 output 스트림을 타고 클라이언트에게 전달
                        message -> sessionSink.tryEmitNext(message),
                        error -> {},
                        () -> {}
                );
    }
}
