import React from 'react';
import Layout from './layout/Layout';

import { createGlobalStyle } from 'styled-components';

import LoginPage from './pages/LoginPage';

const GlobalStyle = createGlobalStyle`
  body {
    font-family: 'Noto Sans KR', sans-serif;
    background-color: #f2f4f7;
    margin: 0;
    padding: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
`;

function App() {
  return (
    <>
      <GlobalStyle />
      <Layout>
        <LoginPage />
      </Layout>
    </>
  )
}

export default App
