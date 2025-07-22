package com.example.chat_mvc.controller;

import com.example.chat_mvc.dto.ApiResponse;
import com.example.chat_mvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/entry/{userId}")
    public ApiResponse<Void> entryUser(@PathVariable String userId) {
        userService.entryUser(userId);
        return ApiResponse.ok(null);
    }
}
