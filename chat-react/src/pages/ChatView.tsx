import React, { useEffect } from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';

import { useAppStore } from '@/store/useAppStore';

const ChatHistory = styled.div`
	flex: 1;
	overflow-y: auto;
	padding: 15px;
	background-color: #f9f9f9;
`;

const StyledChat = styled.p`
	padding: 10px 15px;
	border-radius: 20px;
	margin: 10px 0;
	display: block;
	max-width: 70%;
	word-wrap: break-word;
`;
const Left = styled(StyledChat)`
	background-color: #eaeaea;
	color: #333;
	text-align: left;
`;
const Right = styled(StyledChat)`
	background-color: #4A90E2;
	color: #fff;
	text-align: left;
	margin-left: auto;
`;

const MessageInput = styled.input``;
const SendInput = styled.button``;
const Message = styled.div`.
	display: flex;
	padding: 15px;
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

	useEffect(() => {
		setHeaderInfo(true, "아무개");
	}, []);

	return (
		<FlexContainer flexDirection="column">
			<ChatHistory>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
				<Left>오늘 시민공원 근처에서 7시에 가능하신가요?</Left>
				<Right>네 가능해요! 그때 봐요!</Right>
			</ChatHistory>

			<Message>
				<MessageInput type='text' placeholder='메시지를 입력하세요...'></MessageInput>
				<SendInput>전송</SendInput>
			</Message>
		</FlexContainer>
	);
};

export default ChatView;