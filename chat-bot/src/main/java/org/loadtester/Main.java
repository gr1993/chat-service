package org.loadtester;

import org.loadtester.config.ConfigLoader;
import org.loadtester.dto.ChatMessageInfo;
import org.loadtester.dto.ChatRoomInfo;
import org.loadtester.dto.LoadTestConfig;
import org.loadtester.dto.SendMessageInfo;
import org.loadtester.service.ChatHttpClientService;
import org.loadtester.service.ChatWebSocketClientService;
import org.loadtester.util.Logger;
import org.loadtester.util.MessageUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;


public class Main {

    private final static LoadTestConfig config = ConfigLoader.load("config.json");
    private final static ChatHttpClientService chatClient = new ChatHttpClientService(config);

    private final static String ROOM_NAME = "부하테스트방";
    private static Long roomId = 0L;
    private final static String SEND_MESSAGE = MessageUtil.generateRandomMessage(config.getMessageLength());
    private static CountDownLatch userCompletionLatch;

    private final static AtomicLong totalMessageCount = new AtomicLong();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        try {
            roomId = chatClient.createRoom(ROOM_NAME, config.getUserIdPrefix() + "RoomCreator");

            int userCount = config.getUserCount();
            userCompletionLatch = new CountDownLatch(userCount);
            int rampUpTimeSeconds = config.getRampUpTimeSeconds();
            long delayMillis = (rampUpTimeSeconds * 1000L) / userCount;

            for (int i = 1; i < userCount + 1; i++) {
                int userId = i;
                Thread userThread = new Thread(() -> {
                    simulateUser(config.getUserIdPrefix() + userId);
                });
                userThread.start();

                // 각 사용자 생성 사이에 delay 추가
                Thread.sleep(delayMillis);
            }

            userCompletionLatch.await();

            String nowDateTime = LocalDateTime.now().format(formatter);
            Logger.init("test-result.txt");
            Logger.log("테스트 완료 시각 : " + nowDateTime);
            Logger.log("모든 사용자 시뮬레이션이 완료되어 메인 함수를 종료합니다.");
            Logger.log("총 메세지 전송 갯수 : " + totalMessageCount.get() + "개");
            Logger.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void simulateUser(String userId) {
        try {
            chatClient.login(userId);

            ChatWebSocketClientService chatWebSocketClient = new ChatWebSocketClientService(config.getWebSocketEndpoint());
            chatWebSocketClient.connectWebSocket((stompSession -> {
                // 채팅방 생성 구독
                ChatWebSocketClientService.subscribeStomp(stompSession, "/topic/rooms", ChatRoomInfo.class);

                // 채팅방 메세지 구독
                ChatWebSocketClientService.subscribeStomp(stompSession, "/topic/message/" + roomId, ChatMessageInfo.class);
                
                // 채팅방 입장 API 요청
                chatClient.enterRoom(roomId, userId, stompSession.getSessionId());

                new Thread(() -> {
                    try {
                        long messageSendInterval = 1000L; // 1초마다 메시지 전송
                        long chatDurationMillis = config.getChatDurationSeconds() * 1000L;
                        long startTime = System.currentTimeMillis();

                        while (System.currentTimeMillis() - startTime < chatDurationMillis && stompSession.isConnected()) {
                            // 메시지 전송
                            SendMessageInfo messageInfo = new SendMessageInfo(
                                    roomId,
                                    userId,
                                    SEND_MESSAGE
                            );

                            stompSession.send("/api/messages", messageInfo);
                            totalMessageCount.incrementAndGet();

                            Thread.sleep(messageSendInterval);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 인터럽트 상태 복원
                        System.out.println("메시지 전송 스레드 인터럽트됨. 사용자: " + userId);
                    } catch (Exception e) {
                        System.err.println("메시지 전송 중 오류 발생. 사용자 " + userId + ": " + e.getMessage());
                    } finally {
                        try {
                            // 채팅방 퇴장 API 호출
                            chatClient.exitRoom(roomId, userId);
                        } catch (Exception ignored) {}

                        try {
                            if (stompSession.isConnected()) {
                                stompSession.disconnect();
                                System.out.println("웹소켓 세션이 종료되었습니다.");
                            }
                        } catch (Exception ignored) {}

                        userCompletionLatch.countDown();
                    }
                }).start();
            }));
        } catch (Exception ex) {
            ex.printStackTrace();
            userCompletionLatch.countDown();
        }
    }
}