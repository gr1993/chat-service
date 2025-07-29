package org.loadtester;

import org.loadtester.config.ConfigLoader;
import org.loadtester.dto.LoadTestConfig;
import org.loadtester.service.HttpClientService;

public class Main {

    private final static HttpClientService httpClientService = new HttpClientService();
    private final static LoadTestConfig config = ConfigLoader.load("config.json");

    public static void main(String[] args) {
        try {
            int userCount = 100;

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
        login(userId);
    }

    private static void login(String userId) {
        httpClientService.post("http://localhost:8080/api/user/entry/" + userId, null);
    }

    private static void enterRoom(String userId) {

    }
}