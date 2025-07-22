import React, { useState, useEffect } from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';
import RoundInput from '@/components/common/RoundInput';
import PrimaryButton from '@/components/common/PrimaryButton';

import { useUserState } from '@/store/useUserStore';
import { useNavigate } from 'react-router-dom';

const LoginBox = styled.form`
  width: 380px;
  background-color: #ffffff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  margin: 0px 20px;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
`;

const LoginPage: React.FC = () => {
  const [inputId, setInputId] = useState('');
  const { isLoggedIn, login } = useUserState();
  const navigate = useNavigate();

  useEffect(() => {
    if (isLoggedIn) navigate('/room');
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputId(e.target.value);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputId) {
      alert('아이디를 입력하세요.');
      return;
    }

    login(inputId);
    navigate('/room');
  };

  return (
    <FlexContainer>
      <LoginBox onSubmit={handleSubmit}>
        <RoundInput 
          type="text" 
          placeholder="아이디를 입력하세요" 
          value={inputId}
          onChange={handleChange}
        />
        <PrimaryButton type="submit">입장</PrimaryButton>
      </LoginBox>
    </FlexContainer>
  );
};

export default LoginPage;