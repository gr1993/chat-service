import React from 'react';
import styled from 'styled-components';

import FlexContainer from '@/components/common/FlexContainer';
import RoundInput from '@/components/common/RoundInput';
import PrimaryButton from '@/components/common/PrimaryButton';

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
  return (
    <FlexContainer>
      <LoginBox>
        <RoundInput type="text" placeholder="아이디를 입력하세요" required />
        <PrimaryButton type="submit">입장</PrimaryButton>
      </LoginBox>
    </FlexContainer>
  );
};

export default LoginPage;