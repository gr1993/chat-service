package com.example.chat_mvc.service;

import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;


    @Test
    void entryUser_성공() {
        // given
        String userId = "abc";
        when(userRepository.existsById(userId)).thenReturn(false);

        // when
        userService.entryUser(userId);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void entryUser_실패_아이디중복() {
        // given
        String userId = "abc";
        when(userRepository.existsById(userId)).thenReturn(true);

        // when & then
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.entryUser(userId)
        );
    }
}
