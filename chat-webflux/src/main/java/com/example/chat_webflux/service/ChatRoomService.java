package com.example.chat_webflux.service;

import com.example.chat_webflux.dto.ChatRoomInfo;
import com.example.chat_webflux.entity.ChatRoom;
import com.example.chat_webflux.entity.User;
import com.example.chat_webflux.repository.ChatRoomRepository;
import com.example.chat_webflux.repository.UserRepository;
import com.example.chat_webflux.websocket.ChatRoomManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomManager chatRoomManager;
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 전체 채팅방 리스트 조회
     */
    public Mono<List<ChatRoomInfo>> getRoomList() {
        return chatRoomRepository.findAll()
                .map(ChatRoomInfo::new)
                .collectList();
    }

    /**
     * 채팅방 만들기
     */
    public Mono<Void> createRoom(String name) {
        long id = idGenerator.incrementAndGet();
        ChatRoom newChatRoom = new ChatRoom(id, name);
        chatRoomRepository.save(newChatRoom);

        // 채팅방 생성을 구독자들에게 알림
        try {
            Sinks.Many<String> serverSinks = chatRoomManager.getChatServerSinks();
            serverSinks.tryEmitNext(objectMapper.writeValueAsString(new ChatRoomInfo(newChatRoom)));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        return Mono.empty();
    }

    /**
     * 채팅방에 입장
     */
    public Mono<Void> enterRoom(Long roomId, String userId) {
        return validateRoomAndUser(roomId, userId)
                .flatMap(ru -> {
                    ChatRoom room = ru.room();
                    User user = ru.user();
                    room.getUserMap().put(user.getId(), user);

                    Mono<Void> updateMono = chatRoomRepository.update(room);
                    Mono<Void> broadcastMono = chatMessageService.broadcastSystemMsg(room, userId, userId + "님이 입장하셨습니다.");

                    return updateMono.then(broadcastMono);
                });
    }

    /**
     * 채팅방에서 퇴장
     */
    public Mono<Void> exitRoom(Long roomId, String userId) {
        return validateRoomAndUser(roomId, userId)
                .flatMap(ru -> {
                    ChatRoom room = ru.room();
                    User user = ru.user();
                    room.getUserMap().remove(user.getId());

                    Mono<Void> updateMono = chatRoomRepository.update(room);
                    Mono<Void> broadcastMono = chatMessageService.broadcastSystemMsg(room, userId, userId + "님이 퇴장하셨습니다.");

                    return updateMono.then(broadcastMono);
                });
    }

    private Mono<RoomUser> validateRoomAndUser(Long roomId, String userId) {
        Mono<ChatRoom> roomMono = chatRoomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("존재하지 않은 채팅방입니다.")));

        Mono<User> userMono = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("존재하지 않은 사용자입니다.")));

        return Mono.zip(roomMono, userMono)
                .map(tuple -> new RoomUser(tuple.getT1(), tuple.getT2()));
    }

    private record RoomUser(ChatRoom room, User user) {}
}
