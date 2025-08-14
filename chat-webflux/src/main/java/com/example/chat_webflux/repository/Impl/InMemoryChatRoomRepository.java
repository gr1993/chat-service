package com.example.chat_webflux.repository.Impl;

import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.repository.ChatRoomRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryChatRoomRepository implements ChatRoomRepository {

    /**
     * 읽기가 많고 쓰기가 적을 것으로 예상되어 CopyOnWriteArrayList 사용
     */
    private final List<ChatRoom> chatRoomList = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> save(ChatRoom chatRoom) {
        chatRoomList.add(chatRoom);
        return Mono.empty();
    }

    @Override
    public Flux<ChatRoom> findAll() {
        return Flux.fromIterable(new ArrayList<>(chatRoomList));
    }

    @Override
    public Mono<ChatRoom> findById(Long roomId) {
        return Mono.justOrEmpty(
                chatRoomList.stream()
                        .filter(r -> roomId.equals(r.getId()))
                        .findAny()
        );
    }

    public void clear() {
        chatRoomList.clear();
    }
}
