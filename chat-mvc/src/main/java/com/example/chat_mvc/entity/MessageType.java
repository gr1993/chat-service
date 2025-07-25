package com.example.chat_mvc.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    message("사용자"),
    system("시스템");

    private final String description;
}