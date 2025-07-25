package com.example.chat_mvc.dto;

import com.example.chat_mvc.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEnterInfo {
    String userId;

    public UserEnterInfo(User user) {
        this.userId = user.getId();
    }
}
