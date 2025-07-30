package org.loadtester.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.loadtester.dto.ApiResponse;
import org.loadtester.dto.ChatRoomInfo;
import org.loadtester.dto.LoadTestConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class ChatHttpClientService {
    private final LoadTestConfig config;
    private final HttpClientService httpClientService = new HttpClientService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpHeaders headers;

    public ChatHttpClientService(LoadTestConfig config) {
        this.config = config;

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 사용자 로그인(서버에 입장)
     */
    public void login(String userId) {
        httpClientService.post(config.getRestApiBaseUrl() + "/api/user/entry/" + userId, null);
    }

    /**
     * 채팅방 생성
     */
    public Long createRoom(String roomName) {
        login("roomCreator");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", roomName);
        httpClientService.post(config.getRestApiBaseUrl() + "/api/room", headers, formData);

        // 생성한 채팅방 roomId 찾기
        ApiResponse<List<ChatRoomInfo>> response = fetchAndParse(config.getRestApiBaseUrl() + "/api/room", null, new TypeReference<ApiResponse<List<ChatRoomInfo>>>() {});
        List<ChatRoomInfo> roomInfoList = response.getData();
        return roomInfoList.stream()
                .filter(r -> roomName.equals(r.getRoomName()))
                .map(ChatRoomInfo::getRoomId)
                .findAny()
                .orElse(null);
    }

    /**
     * 채팅방에 입장
     */
    public void enterRoom(Long roomId, String userId, String sessionId) {
        HttpHeaders sessionHeaders = new HttpHeaders();
        sessionHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        sessionHeaders.add("X-Session-Id", sessionId);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userId", userId);
        httpClientService.post(config.getRestApiBaseUrl() + "/api/room/" + roomId + "/enter", sessionHeaders, formData);
    }

    /**
     * 채팅방에서 퇴장
     */
    public void exitRoom(Long roomId, String userId) {
        HttpHeaders sessionHeaders = new HttpHeaders();
        sessionHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userId", userId);
        httpClientService.post(config.getRestApiBaseUrl() + "/api/room/" + roomId + "/exit", sessionHeaders, formData);
    }


    private <T> T fetchAndParse(String url, HttpHeaders headers, TypeReference<T> typeRef) {
        try {
            String json = httpClientService.get(url, headers, String.class);
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("JSON 파싱 실패: " + url, ex);
        }
    }
}
