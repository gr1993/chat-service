import React, { useState, useEffect } from 'react';

import FlexContainer from '@/components/common/FlexContainer';
import ChatRoom from '@/components/ChatRoom';
import ChatRoomCreateModal from '@/components/ChatRoomCreateModal';

import { useAppStore } from '@/store/useAppStore';
import { useChatStore } from '@/store/useChatStore';
import { useNavigate } from 'react-router-dom';
import { handleApiResponse } from '@/api/apiUtils';
import type { ChatRoomInfo } from '@/api/types';
import { getRoomList, createRoom } from '@/api/chatRoom';
import { useWebSocket } from '@/common/useWebSocket';

const ChatRooms: React.FC = () => {
  const [roomList, setRoomList] = useState<ChatRoomInfo[] | null>([]);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const { setHeaderInfo } = useAppStore();
  const { setCurrentRoom } = useChatStore();
  const navigate = useNavigate();

  // 채팅방 생성 구독
  useWebSocket((client) => {
    client.subscribe("/topic/rooms", (message) => {
      const payload: ChatRoomInfo = JSON.parse(message.body);
      setRoomList((prev) => [...(prev ?? []), payload]);
    });
  });

  useEffect(() => {
    setHeaderInfo(true, "채팅방 목록");

    // 전체 채팅방 목록 조회 API
    handleApiResponse(
      getRoomList(),
      (data) => {
        setRoomList(data);
      }
    );

    return () => {};
  }, []);

  const createChatRoom = (roomName: string) => {
    // 전체 채팅방 생성 API
    handleApiResponse(
      createRoom(roomName),
      () => {
        setIsModalOpen(false);
      }
    );
  }

  const handleRoomClick = (room: ChatRoomInfo) => {
    setCurrentRoom({ id: room.roomId, name: room.roomName });
    navigate("/chat");
  }

  return (
    <FlexContainer $flexDirection="column" $justifyContent="flex-start">
      {roomList?.map((room) => (
        <ChatRoom key={room.roomId} room={room} onClick={handleRoomClick} />
      ))}
      {isModalOpen && (
        <ChatRoomCreateModal
          onClose={() => setIsModalOpen(false)}
          onCreate={createChatRoom}
        />
      )}
    </FlexContainer>
  );
};

export default ChatRooms;