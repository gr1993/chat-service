package org.loadtester.service;

import org.loadtester.dto.LoadTestConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ChatClientService {
    private final LoadTestConfig config;
    private final HttpClientService httpClientService = new HttpClientService();

    public ChatClientService(LoadTestConfig config) {
        this.config = config;
    }

    public void login(String userId) {
        httpClientService.post(config.getRestApiBaseUrl() + "/api/user/entry/" + userId, null);
    }

    public void createRoom(String roomName) {
        login("roomCreator");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", roomName);
        httpClientService.post(config.getRestApiBaseUrl() + "/api/room", headers, formData);
    }

    public void enterRoom(String userId) {

    }
}
