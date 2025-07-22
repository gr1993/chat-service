import { create } from 'zustand'

interface UserState {
  isLoggedIn: boolean
  id: string
  login: (id: string) => void
  logout: () => void
}

export const useUserStore = create<UserState>((set) => {
  const savedUserId = localStorage.getItem('userId');
  const savedIsLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

  return {
    isLoggedIn: savedIsLoggedIn || false,
    id: savedUserId || '',
    login: (id: string) => {
      set({ isLoggedIn: true, id });
      localStorage.setItem('userId', id);
      localStorage.setItem('isLoggedIn', 'true');
    },
    logout: () => {
      set({ isLoggedIn: false, id: '' });
      localStorage.removeItem('userId');
      localStorage.removeItem('isLoggedIn');
    },
  };
});