package org.loadtester;

import org.loadtester.config.ConfigLoader;
import org.loadtester.dto.ChatMessageInfo;
import org.loadtester.dto.ChatRoomInfo;
import org.loadtester.dto.LoadTestConfig;
import org.loadtester.service.ChatHttpClientService;
import org.loadtester.service.ChatWebSocketClientService;
import org.springframework.messaging.simp.stomp.StompSession;


public class Main {

    private final static LoadTestConfig config = ConfigLoader.load("config.json");
    private final static ChatHttpClientService chatClient = new ChatHttpClientService(config);

    private final static String ROOM_NAME = "부하테스트방";
    private static Long roomId = 0L;

    public static void main(String[] args) {
        try {
            roomId = chatClient.createRoom(ROOM_NAME);

            int userCount = config.getUserCount();
            int rampUpTimeSeconds = config.getRampUpTimeSeconds();
            long delayMillis = (rampUpTimeSeconds * 1000L) / userCount;

            for (int i = 1; i < userCount + 1; i++) {
                int userId = i;
                Thread userThread = new Thread(() -> {
                    simulateUser("User" + userId);
                });
                userThread.start();

                // 각 사용자 생성 사이에 delay 추가
                Thread.sleep(delayMillis);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void simulateUser(String userId) {
        try {
            chatClient.login(userId);

            ChatWebSocketClientService chatWebSocketClient = new ChatWebSocketClientService(config.getWebSocketEndpoint());
            StompSession session = chatWebSocketClient.connectWebSocket((stompSession -> {
                // 채팅방 생성 구독
                ChatWebSocketClientService.subscribeStomp(stompSession, "/topic/rooms", ChatRoomInfo.class);

                // 채팅방 메세지 구독
                ChatWebSocketClientService.subscribeStomp(stompSession, "/topic/message/" + roomId, ChatMessageInfo.class);
                
                // 채팅방 입장 API 요청
                chatClient.enterRoom(roomId, userId, stompSession.getSessionId());
            }));

            // 사용자가 채팅방에 머무는 시간 (초 → 밀리초)
            long durationMillis = config.getChatDurationSeconds() * 1000L;
            Thread.sleep(durationMillis);

            // 채팅방 퇴장 API 호출
            chatClient.exitRoom(roomId, userId);

            if (session.isConnected()) {
                session.disconnect();
                System.out.println("웹소켓 세션이 종료되었습니다.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}