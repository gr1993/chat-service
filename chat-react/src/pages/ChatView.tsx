import React, { useState, useEffect } from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';
import ChatMessage from '@/components/ChatMessage';
import MessageBox from '@/components/MessageBox';

import { useAppStore } from '@/store/useAppStore';
import { useChatStore } from '@/store/useChatStore';
import type { ChatMessageInfo } from '@/api/types';

const ChatHistory = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: #f9f9f9;
`;

const ChatView: React.FC = () => {
  const { setHeaderInfo } = useAppStore();
  const { currentRoom } = useChatStore();
  const [messageList, setMessageList] = useState<ChatMessageInfo[] | null>([]);

  useEffect(() => {
    setHeaderInfo(true, currentRoom?.name ?? '');

    // 채팅방 입장 알림 구독

  }, []);

  return (
    <FlexContainer $flexDirection="column">
      <ChatHistory>
        {messageList?.map((message) => (
          <ChatMessage key={message.id} message={message} />
        ))}
      </ChatHistory>

      <MessageBox onSend={(message) => { alert(message); }} />
    </FlexContainer>
  );
};

export default ChatView;