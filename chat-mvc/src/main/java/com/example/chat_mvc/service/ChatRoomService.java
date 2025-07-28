package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.ChatRoomRepository;
import com.example.chat_mvc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * 전체 채팅방 리스트 조회
     */
    public List<ChatRoomInfo> getRoomList() {
        return chatRoomRepository.findAll()
                .stream()
                .map(ChatRoomInfo::new)
                .toList();
    }

    /**
     * 채팅방 만들기
     */
    public void createRoom(String name) {
        long id = idGenerator.incrementAndGet();
        ChatRoom newChatRoom = new ChatRoom(id, name);
        chatRoomRepository.save(newChatRoom);

        // 채팅방 생성을 구독자들에게 알림
        messagingTemplate.convertAndSend("/topic/rooms", new ChatRoomInfo(newChatRoom));
    }

    /**
     * 채팅방에 입장
     */
    public void enterRoom(Long roomId, String userId) {
        RoomUser ru = validateRoomAndUser(roomId, userId);

        ChatRoom room = ru.room();
        User user = ru.user();
        room.getUserMap().put(user.getId(), user);
        chatRoomRepository.update(room);

        // 채팅방에 사용자 입장을 구독자들에게 알림
        String msg = userId + "님이 입장하셨습니다.";
        chatMessageService.broadcastSystemMsg(room, userId, msg);
    }

    /**
     * 채팅방에서 퇴장
     */
    public void exitRoom(Long roomId, String userId) {
        RoomUser ru = validateRoomAndUser(roomId, userId);

        ChatRoom room = ru.room();
        User user = ru.user();
        room.getUserMap().remove(user.getId());
        chatRoomRepository.update(room);

        // 채팅방에 사용자 퇴장을 구독자들에게 알림
        String msg = userId + "님이 퇴장하셨습니다.";
        chatMessageService.broadcastSystemMsg(room, userId, msg);
    }

    private RoomUser validateRoomAndUser(Long roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 채팅방입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 사용자입니다."));

        return new RoomUser(room, user);
    }

    private record RoomUser(ChatRoom room, User user) {}

}
