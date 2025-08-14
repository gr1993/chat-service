package com.example.chat_webflux.repository;

import com.example.chat_webflux.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<Boolean> existsById(String userId);
    Mono<User> save(User user);
    Mono<Void> delete(String userId);
    Mono<User> findById(String userId);
    Flux<User> findAll();
}
