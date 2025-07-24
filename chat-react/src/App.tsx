import React from 'react';
import Layout from './layout/Layout';

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { createGlobalStyle } from 'styled-components';

import RequireAuth from './components/RequireAuth';

import LoginPage from './pages/LoginPage';
import ChatRooms from './pages/ChatRooms';
import ChatView from './pages/ChatView';

const GlobalStyle = createGlobalStyle`
  /* 기본 리셋 */
  *, *::before, *::after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

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

  a {
    text-decoration: none;
    color: inherit;
  }

  ul, ol {
    list-style: none;
  }

  button {
    border: none;
    background: none;
    cursor: pointer;
    font: inherit;
  }
`;

function App() {
  return (
    <>
      <GlobalStyle />
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<LoginPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/room" element={<RequireAuth><ChatRooms /></RequireAuth>} />
            <Route path="/chat" element={<RequireAuth><ChatView /></RequireAuth>} />
          </Routes>
        </Layout>
      </BrowserRouter>
    </>
  )
}

export default App
