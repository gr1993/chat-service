import React, { useState, useEffect } from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';
import ChatMessage from '@/components/ChatMessage';
import MessageBox from '@/components/MessageBox';

import { useAppStore } from '@/store/useAppStore';
import { useUserStore } from '@/store/useUserStore';
import { useChatStore } from '@/store/useChatStore';
import { useWebSocket } from '@/common/useWebSocket';
import { handleApiResponse } from '@/api/apiUtils';
import type { ChatMessageInfo } from '@/api/types';
import { enterRoom } from '@/api/chatRoom';

const ChatHistory = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: #f9f9f9;
`;

const ChatView: React.FC = () => {
  const { setHeaderInfo } = useAppStore();
  const { currentRoom } = useChatStore();
  const { id: userId } = useUserStore();
  const [messageList, setMessageList] = useState<ChatMessageInfo[] | null>([]);

  // 채팅방 메세지, 입장, 퇴장 정보 구독
  useWebSocket((client) => {
    client.subscribe(`/topic/message/${currentRoom?.id}`, (message) => {
      const payload: ChatMessageInfo = JSON.parse(message.body);
      setMessageList((prev) => [...(prev ?? []), payload]);
    });

    setTimeout(() => {
      // 채팅방 입장 API
      if (currentRoom) {
        handleApiResponse(
          enterRoom(currentRoom.id, userId),
          () => {}
        );
      }
    }, 100);
  });

  useEffect(() => {
    setHeaderInfo(true, currentRoom?.name ?? '');
  }, []);

  return (
    <FlexContainer $flexDirection="column">
      <ChatHistory>
        {messageList?.map((message) => (
          <ChatMessage key={message.messageId} message={message} />
        ))}
      </ChatHistory>

      <MessageBox onSend={(message) => { alert(message); }} />
    </FlexContainer>
  );
};

export default ChatView;