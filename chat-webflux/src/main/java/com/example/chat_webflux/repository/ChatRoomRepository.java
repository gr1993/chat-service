package com.example.chat_webflux.repository;

import com.example.chat_webflux.entity.ChatRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository {
    Mono<Void> save(ChatRoom chatRoom);
    Flux<ChatRoom> findAll();
    Mono<ChatRoom> findById(Long roomId);
    default Mono<Void> update(ChatRoom chatRoom) {
        return Mono.empty();
    }
}