package com.example.chat_mvc.repository;

import com.example.chat_mvc.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void save_findById_성공() {
        // given
        String userId = "abc";
        User user = new User(userId);

        // when
        userRepository.save(user);
        Optional<User> savedUser = userRepository.findById(userId);

        // then
        assertTrue(savedUser.isPresent());
        assertEquals(userId, savedUser.get().getId());
    }

    @Test
    void delete_성공() {
        // given
        String userId = "abc";
        User user = new User(userId);
        userRepository.save(user);

        // when
        userRepository.delete(userId);

        // then
        assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    void findAll_성공() {
        // given
        userRepository.save(new User("abc"));
        userRepository.save(new User("def"));

        // when
        List<User> users = userRepository.findAll();

        // then
        assertEquals(2, users.size());
    }

}
