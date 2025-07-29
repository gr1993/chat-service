package org.example;

import org.example.dto.ApiResponse;
import org.example.service.HttpClientService;

public class Main {

    private static HttpClientService httpClientService = new HttpClientService();

    public static void main(String[] args) {
        login("park");
    }

    private static void login(String userId) {
        httpClientService.post("http://localhost:8080/api/user/entry/" + userId, null);
    }
}