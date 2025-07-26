import { useEffect, useRef } from 'react';
import Layout from './layout/Layout';

import { BrowserRouter, Routes, Route } from 'react-router-dom';

import { connectWebSocket, disconnectWebSocket } from './common/socketClient';

import GlobalStyle from './components/GlobalStyle'
import RequireAuth from './components/RequireAuth';

import LoginPage from './pages/LoginPage';
import ChatRooms from './pages/ChatRooms';
import ChatView from './pages/ChatView';

const ESCAPE_STRICT_MODE = import.meta.env.VITE_ESCAPE_STRICT_MODE;

function App() {
  const isMounted = useRef(false);

  useEffect(() => {
    if (ESCAPE_STRICT_MODE !== 'Y' || isMounted.current) {
      connectWebSocket();

      return () => {
        disconnectWebSocket();
      };
    } else {
      isMounted.current = true;
    }
  }, []);

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
