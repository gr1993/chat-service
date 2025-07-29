package org.loadtester;

import org.loadtester.config.ConfigLoader;
import org.loadtester.dto.LoadTestConfig;
import org.loadtester.service.HttpClientService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class Main {

    private final static String ROOM_NAME = "부하테스트방";
    private final static HttpClientService httpClientService = new HttpClientService();
    private final static LoadTestConfig config = ConfigLoader.load("config.json");

    public static void main(String[] args) {
        try {
            createRoom();

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
        httpClientService.post(config.getRestApiBaseUrl() + "/api/user/entry/" + userId, null);
    }

    public static void createRoom() {
        login("roomCreator");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", ROOM_NAME);
        httpClientService.post(config.getRestApiBaseUrl() + "/api/room", headers, formData);
    }

    private static void enterRoom(String userId) {

    }
}