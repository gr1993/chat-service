import { create } from 'zustand'

interface UserState {
  isLoggedIn: boolean
  id: string
  login: (id: string) => void
  logout: () => void
}

export const useUserState = create<UserState>((set) => ({
  isLoggedIn: false,
  id: '',
  login: (id: string) => set({ isLoggedIn: true, id }),
  logout: () => set({ isLoggedIn: false, id: '' }),
}))