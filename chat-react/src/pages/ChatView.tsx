import React, { useState } from 'react';
import styled from 'styled-components';

import type { IMessage } from '@stomp/stompjs';

import FlexContainer from '@/components/common/FlexContainer';
import ChatMessage from '@/components/ChatMessage';
import MessageBox from '@/components/MessageBox';

import { useAppStore } from '@/store/useAppStore';
import { useUserStore } from '@/store/useUserStore';
import { useChatStore } from '@/store/useChatStore';
import { useChatSubscribe } from '@/hooks/useChatSubscribe';
import { useStrictEffect } from '@/hooks/useStrictEffect';
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
  useChatSubscribe(`/topic/message/${currentRoom?.id}`, (message: IMessage) => {
    const payload: ChatMessageInfo = JSON.parse(message.body);
    setMessageList((prev) => [...(prev ?? []), payload]);
  });

  useStrictEffect(() => {
    setHeaderInfo(true, currentRoom?.name ?? '');

    setTimeout(() => {
      // 채팅방 입장 API
      if (currentRoom) {
        handleApiResponse(
          enterRoom(currentRoom.id, userId),
          () => {}
        );
      }
    }, 100);
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