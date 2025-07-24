import React, { useState, useEffect, useRef } from 'react';
import SockJS from "sockjs-client";
import { CompatClient, Stomp } from "@stomp/stompjs";

import FlexContainer from '@/components/common/FlexContainer';
import ChatRoom from '@/components/ChatRoom';
import ChatRoomCreateModal from '@/components/ChatRoomCreateModal';

import { useAppStore } from '@/store/useAppStore';
import { handleApiResponse } from '@/api/apiUtils';
import type { ChatRoomInfo } from '@/api/types';
import { getRoomList, createRoom } from '@/api/chatRoom';

const SOCKET_URL = import.meta.env.VITE_API_URL + '/ws';

const ChatRooms: React.FC = () => {
  const [roomList, setRoomList] = useState<ChatRoomInfo[] | null>([]);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const { setHeaderInfo } = useAppStore();
  const stompClient = useRef<CompatClient | null>(null);

  useEffect(() => {
    setHeaderInfo(true, "채팅방 목록");

    // 전체 채팅방 목록 조회 API
    handleApiResponse(
      getRoomList(),
      (data) => {
        setRoomList(data);
      }
    );

    // 웹소켓 연결
    const socket = new SockJS(SOCKET_URL);
    const client = Stomp.over(socket);

    client.connect({}, () => {
      // 채팅방 생성 구독
      client.subscribe("/topic/rooms", (message) => {
        const payload: ChatRoomInfo = JSON.parse(message.body);
        setRoomList((prev) => [...(prev ?? []), payload]);
      });
    });

    stompClient.current = client;

    return () => {
      client.disconnect(() => {
        console.log("STOMP 연결 종료");
      });
    };
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


  return (
    <FlexContainer $flexDirection="column" $justifyContent="flex-start">
      {roomList?.map((room) => (
        <ChatRoom key={room.roomId} room={room} />
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