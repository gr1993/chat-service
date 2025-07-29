package org.loadtester;

import org.loadtester.config.ConfigLoader;
import org.loadtester.dto.LoadTestConfig;
import org.loadtester.service.ChatClientService;


public class Main {

    private final static String ROOM_NAME = "부하테스트방";
    private final static LoadTestConfig config = ConfigLoader.load("config.json");
    private final static ChatClientService chatClient = new ChatClientService(config);

    public static void main(String[] args) {
        try {
            chatClient.createRoom(ROOM_NAME);

            int userCount = config.getUserCount();

            for (int i = 1; i < userCount + 1; i++) {
                int userId = i;
                Thread userThread = new Thread(() -> {
                    simulateUser("사용자" + userId);
                });
                userThread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void simulateUser(String userId) {
        chatClient.login(userId);
    }


}