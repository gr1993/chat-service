package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatMessageInfo;
import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.entity.MessageType;
import com.example.chat_mvc.entity.User;
import com.example.chat_mvc.repository.ChatRoomRepository;
import com.example.chat_mvc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

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
        Optional<ChatRoom> roomOptional = chatRoomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않은 채팅방입니다.");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않은 사용자입니다.");
        }

        ChatRoom room = roomOptional.get();
        User user = userOptional.get();
        room.getUserQueue().add(user);
        chatRoomRepository.update(room);

        // 채팅방에 사용자 입장을 구독자들에게 알림
        ChatMessageInfo messageInfo = new ChatMessageInfo();
        messageInfo.setMessageId(1L);
        messageInfo.setSenderId(user.getId());
        messageInfo.setMessage(messageInfo.getSenderId() + "님이 입장하셨습니다.");
        messageInfo.setSendDt(LocalDateTime.now().toString());
        messageInfo.setType(MessageType.system.name());
        messagingTemplate.convertAndSend("/topic/message/" + roomId, messageInfo);
    }

}
