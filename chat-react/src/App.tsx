import Layout from './layout/Layout';

import { BrowserRouter, Routes, Route } from 'react-router-dom';

import GlobalStyle from './components/GlobalStyle'
import RequireAuth from './components/RequireAuth';

import LoginPage from './pages/LoginPage';
import ChatRooms from './pages/ChatRooms';
import ChatView from './pages/ChatView';

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
