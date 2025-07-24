import React, { useState, useEffect } from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';
import ChatMessage from '@/components/ChatMessage';

import { useAppStore } from '@/store/useAppStore';
import { useChatStore } from '@/store/useChatStore';
import type { ChatMessageInfo } from '@/api/types';

const ChatHistory = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: #f9f9f9;
`;

const MessageInput = styled.input``;
const SendInput = styled.button``;
const MessageBox = styled.div`
  display: flex;
  padding: 15px 15px;
  width: 100%;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;

  ${MessageInput} {
    flex-grow: 1;
    padding: 12px 15px;
    border-radius: 25px;
    border: 1px solid #ddd;
    font-size: 16px;
    outline: none;
  }

  ${SendInput} {
    background-color: #4A90E2;
    border: none;
    padding: 12px 20px;
    border-radius: 25px;
    color: white;
    margin-left: 10px;
    cursor: pointer;
    font-size: 16px;

    &:hover {
      background-color: #357ABD;
    }
  }
`;


const ChatView: React.FC = () => {
  const { setHeaderInfo } = useAppStore();
  const { currentRoom } = useChatStore();
  const [messageList, setMessageList] = useState<ChatMessageInfo[] | null>([]);

  useEffect(() => {
    setHeaderInfo(true, currentRoom?.name ?? '');
  }, []);

  return (
    <FlexContainer $flexDirection="column">
      <ChatHistory>
        {messageList?.map((message) => (
          <ChatMessage key={message.id} message={message} />
        ))}
      </ChatHistory>

      <MessageBox>
        <MessageInput type='text' placeholder='메시지를 입력하세요...'></MessageInput>
        <SendInput>전송</SendInput>
      </MessageBox>
    </FlexContainer>
  );
};

export default ChatView;