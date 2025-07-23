package com.example.chat_mvc.controller;

import com.example.chat_mvc.dto.ApiResponse;
import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ChatRoomRestController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ApiResponse<List<ChatRoomInfo>> getRoomList() {
        List<ChatRoomInfo> roomList = chatRoomService.getRoomList();
        return ApiResponse.ok(roomList);
    }

    @PostMapping
    public ApiResponse<Void> createRoom(@RequestParam String name) {
        chatRoomService.createRoom(name);
        return ApiResponse.ok(null);
    }

}
