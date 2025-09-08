package com.example.chat_webflux.service;

import com.example.chat_webflux.util.StompFrameParser;
import com.example.chat_webflux.common.ChatRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 웹소켓 STOMP 메시지 구독용 서비스(STOMP 기능만 구현)
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final ChatRoomManager chatRoomManager;

    private final ConcurrentHashMap<String, Disposable> subscriptions = new ConcurrentHashMap<>();

    /**
     * 채팅방 메시지 구독
     */
    public void subscribeRoomMessage(Sinks.Many<String> sessionSink,
                                     String destination,
                                     String sessionId,
                                     String subscriptionId) {
        String roomId = destination.substring("/topic/message/".length());

        // 해당 채팅방의 Sinks.Many를 가져오거나 없으면 새로 생성
        Sinks.Many<String> roomSink = chatRoomManager.getRoomSink(roomId);
        subscribe(roomSink, sessionSink, destination, sessionId, subscriptionId);
    }

    /**
     * 채팅방 생성 구독
     */
    public void subscribeRoomCreate(Sinks.Many<String> sessionSink,
                                    String destination,
                                    String sessionId,
                                    String subscriptionId) {

        Sinks.Many<String> serverSinks = chatRoomManager.getChatServerSinks();
        subscribe(serverSinks, sessionSink, destination, sessionId, subscriptionId);
    }

    /**
     * 구독 종료
     */
    public void unSubscribe(String sessionId, String subscriptionId) {
        String key = sessionId + ":" + subscriptionId;
        Disposable disposable = subscriptions.remove(key); // 맵에서 제거
        if (disposable != null) {
            disposable.dispose(); // 구독 취소
        }
    }

    private void subscribe(Sinks.Many<String> multSink,
                                 Sinks.Many<String> sessionSink,
                                 String destination,
                                 String sessionId,
                                 String subscriptionId) {

        // multSink에서 발생한 데이터를 현재 세션(sessionSink)에 전달하도록 구독
        Disposable disposable = multSink.asFlux()
                .map(json -> {
                    // 이제 STOMP 프레임이 된 문자열을 sessionSink에 발행
                    return StompFrameParser.createStompMessageFrame(destination, subscriptionId, json);
                })
                .subscribe(
                        // 이 메시지는 handle()의 output 스트림을 타고 클라이언트에게 전달
                        message -> sessionSink.tryEmitNext(message),
                        error -> {},
                        () -> {}
                );

        // 구독 취소를 위해 구독 정보 저장
        String key = sessionId + ":" + subscriptionId;
        subscriptions.put(key, disposable);
    }
}
