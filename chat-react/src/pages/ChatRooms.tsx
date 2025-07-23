import React, { useState, useEffect } from 'react';

import FlexContainer from '@/components/common/FlexContainer';
import ChatRoom from '@/components/ChatRoom';

import { useAppStore } from '@/store/useAppStore';
import { handleApiResponse } from '@/api/apiUtils';
import type { ChatRoomInfo } from '@/api/types';
import { getRoomList } from '@/api/chatRoom';


const ChatRooms: React.FC = () => {
  const [roomList, setRoomList] = useState<ChatRoomInfo[] | null>([]);
  const { setHeaderInfo } = useAppStore();

  useEffect(() => {
    setHeaderInfo(true, "채팅방 목록");

    // 전체 채팅방 목록 조회 API
    handleApiResponse(
      getRoomList(),
      (data) => {
        setRoomList(data);
      }
    );
  }, []);

  return (
    <FlexContainer $flexDirection="column" $justifyContent="flex-start">
      {roomList?.map((room) => (
        <ChatRoom key={room.roomId} room={room} />
      ))}
    </FlexContainer>
  );
};

export default ChatRooms;