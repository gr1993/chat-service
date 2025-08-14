package com.example.chat_webflux.repository;

import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.entity.User;
import com.example.chat_webflux.repository.Impl.InMemoryChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @BeforeEach
    void setUp() {
        if (chatRoomRepository instanceof InMemoryChatRoomRepository repo) {
            repo.clear();
        }
    }

    @Test
    void save_성공() {
        // given
        ChatRoom chatRoom = new ChatRoom(1L, "park");
        chatRoom.setCreateDt(LocalDateTime.now());

        // when
        chatRoomRepository.save(chatRoom).block();

        // then
        Flux<ChatRoom> roomList = chatRoomRepository.findAll();
        StepVerifier.create(roomList)
                .expectNext(chatRoom)
                .verifyComplete();
    }

    @Test
    void findAll_성공() {
        // given
        chatRoomRepository.save(new ChatRoom(1L, "park")).block();
        chatRoomRepository.save(new ChatRoom(2L, "kang")).block();

        // when
        Flux<ChatRoom> roomList = chatRoomRepository.findAll();

        // then
        StepVerifier.create(roomList)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    void findById_성공() {
        // given
        ChatRoom searchRoom = new ChatRoom(2L, "kang");
        chatRoomRepository.save(new ChatRoom(1L, "park")).block();
        chatRoomRepository.save(searchRoom).block();

        // when
        Mono<ChatRoom> roomMono = chatRoomRepository.findById(2L);

        // then
        StepVerifier.create(roomMono)
                .expectNext(searchRoom)
                .verifyComplete();
    }

    @Test
    void update_성공() {
        // given
        ChatRoom searchRoom = new ChatRoom(1L, "park");
        chatRoomRepository.save(searchRoom);
        ChatRoom updateRoom = chatRoomRepository.findById(1L).block();

        // when
        String userId = "kang";
        User user = new User(userId);
        updateRoom.getUserMap().put(userId, user);
        chatRoomRepository.update(updateRoom).block();

        // then
        ChatRoom roomInfo = chatRoomRepository.findById(1L).block();
        assertEquals(1, roomInfo.getUserMap().size());
    }
}