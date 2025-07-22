package com.example.chat_mvc.repository.impl;

import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public boolean existsById(String userId) {
        return userMap.containsKey(userId);
    }

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public void delete(String userId) {
        userMap.remove(userId);
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }
}