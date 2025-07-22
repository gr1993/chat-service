package com.example.chat_mvc.repository;

import com.example.chat_mvc.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean existsById(String userId);
    void save(User user);
    void delete(String userId);
    Optional<User> findById(String userId);
    List<User> findAll();
}
