package com.example.chat_webflux.repository.Impl;

import com.example.chat_webflux.entity.User;
import com.example.chat_webflux.repository.UserRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Boolean> existsById(String userId) {
        return Mono.just(userMap.containsKey(userId));
    }

    @Override
    public Mono<User> save(User user) {
        userMap.put(user.getId(), user);
        return Mono.just(user);
    }

    @Override
    public Mono<Void> delete(String userId) {
        userMap.remove(userId);
        return Mono.empty();
    }

    @Override
    public Mono<User> findById(String userId) {
        return Mono.justOrEmpty(userMap.get(userId));
    }

    @Override
    public Flux<User> findAll() {
        return Flux.fromIterable(userMap.values());
    }
}
