package com.example.chat_mvc.dto;

import com.example.chat_mvc.entity.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomInfo {
    private Long roomId;
    private String roomName;
    private String lastId;
    private String lastMessage;
    private String lastDt;

    public ChatRoomInfo() {}
    public ChatRoomInfo(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId();
        this.roomName = chatRoom.getName();
    }
}
