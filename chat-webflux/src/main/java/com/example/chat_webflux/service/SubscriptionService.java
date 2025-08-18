package com.example.chat_webflux.service;

import com.example.chat_webflux.util.StompFrameParser;
import com.example.chat_webflux.common.ChatRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

/**
 * 웹소켓 STOMP 메시지 구독용 서비스(STOMP 기능만 구현)
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final ChatRoomManager chatRoomManager;

    /**
     * 채팅방 메시지 구독
     */
    public Disposable subscribeRoomMessage(Sinks.Many<String> sessionSink, String destination, String subscriptionId) {
        String roomId = destination.substring("/topic/message/".length());

        // 해당 채팅방의 Sinks.Many를 가져오거나 없으면 새로 생성
        Sinks.Many<String> roomSink = chatRoomManager.getRoomSink(roomId.toString());
        return subscribe(roomSink, sessionSink, destination, subscriptionId);
    }

    /**
     * 채팅방 생성 구독
     */
    public Disposable subscribeRoomCreate(Sinks.Many<String> sessionSink, String destination, String subscriptionId) {
        Sinks.Many<String> serverSinks = chatRoomManager.getChatServerSinks();
        return subscribe(serverSinks, sessionSink, destination, subscriptionId);
    }

    private Disposable subscribe(Sinks.Many<String> multSink,
                                 Sinks.Many<String> sessionSink,
                                 String destination,
                                 String subscriptionId) {
        // 이 Flux를 현재 세션의 sessionSink에 구독
        return multSink.asFlux()
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
