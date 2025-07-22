import React, { useEffect } from 'react';
import styled from 'styled-components';

import defaultProfile from '../assets/default-profile.png';
import FlexContainer from '@/components/common/FlexContainer';

import { useAppStore } from '@/store/useAppStore';

const ChatRoom = styled.div`
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  width: 100%;

  &:hover {
    background-color: #f9f9f9;
  }
`;

const ProfileImg = styled.img`
  width: 55px;
  height: 55px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 15px;
`;

const NameLast = styled.div``;
const Name = styled.div``;
const LastMessage = styled.div``;
const TimeBadge = styled.div``;
const Time = styled.span``;
const UnreadBadge = styled.span``;

const ChatInfo = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  width: 100%;

  ${NameLast} {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex-grow: 1;
  }

  ${Name} {
    font-weight: bold;
    font-size: 16px;
  }

  ${LastMessage} {
    color: #555;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  ${TimeBadge} {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: center;
    min-width: 60px;  
    margin-left: auto;
    margin-right: 4px;
  }

  ${Time} {
    font-size: 12px;
    color: gray;
  }

  ${UnreadBadge} {
    background-color: #FF3B30;
    color: white;
    font-size: 12px;
    padding: 4px 7px;
    border-radius: 20px;
    margin-left: 5px;
  }
`;


const ChatRooms: React.FC = () => {
  const { setHeaderInfo } = useAppStore();

  useEffect(() => {
    setHeaderInfo(true, "채팅방 목록");
  }, []);

  return (
    <FlexContainer $flexDirection="column" $justifyContent="flex-start">
      <ChatRoom>
        <ProfileImg src={defaultProfile} alt="프로필 이미지" />
        <ChatInfo>
          <NameLast>
            <Name>슬기맘</Name>
            <LastMessage>오늘 7시에 시민공원에서 봐요</LastMessage>
          </NameLast>
          <TimeBadge>
            <Time>오후 2:35</Time>
            <UnreadBadge>2</UnreadBadge>
          </TimeBadge>
        </ChatInfo>
      </ChatRoom>
    </FlexContainer>
  );
};

export default ChatRooms;