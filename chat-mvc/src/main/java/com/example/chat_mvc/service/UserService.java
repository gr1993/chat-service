package com.example.chat_mvc.service;

import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 입장 처리
     */
    public void entryUser(String userId) {
        if (userRepository.existsById(userId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        userRepository.save(new User(userId));
    }
}