import styled from 'styled-components';

interface FlexContainerProps {
  justifyContent?: string;
  alignItems?: string;
  flexDirection?: string;
}

const FlexContainer = styled.div<FlexContainerProps>`
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: ${({ justifyContent }) => justifyContent || 'center'};
  align-items: ${({ alignItems }) => alignItems || 'center'};
  flex-direction: ${({ flexDirection }) => flexDirection || 'row'};
`;

export default FlexContainer;