package com.example.chat_mvc.service;

import com.example.chat_mvc.dto.ChatRoomInfo;
import com.example.chat_mvc.entity.ChatRoom;
import com.example.chat_mvc.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
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

}
