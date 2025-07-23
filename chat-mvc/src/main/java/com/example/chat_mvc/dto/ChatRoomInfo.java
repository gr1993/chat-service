package com.example.chat_mvc.dto;

import lombok.Data;

@Data
public class ChatRoomInfo {
    private Long roomId;
    private String roomName;
    private String lastId;
    private String lastMessage;
    private String lastDt;

    public ChatRoomInfo(Long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
