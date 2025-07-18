import React from 'react';
import styled from 'styled-components';

const StyledHeader = styled.header`
  background-color: #4A90E2;
  color: white;
  padding: 20px;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
`;

const Header: React.FC = () => {
  return (
    <StyledHeader>
      로그인
    </StyledHeader>
  );
};

export default Header;