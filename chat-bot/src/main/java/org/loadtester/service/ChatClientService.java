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

public class ChatClientService {
    private final LoadTestConfig config;
    private final HttpClientService httpClientService = new HttpClientService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatClientService(LoadTestConfig config) {
        this.config = config;
    }

    public void login(String userId) {
        httpClientService.post(config.getRestApiBaseUrl() + "/api/user/entry/" + userId, null);
    }

    public Long createRoom(String roomName) {
        //login("roomCreator");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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

    public void enterRoom(String userId) {

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
