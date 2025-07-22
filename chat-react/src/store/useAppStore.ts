import { create } from 'zustand'

interface AppState {
  isBackButton: boolean;
  headerTitle: string;
  setHeaderInfo: (isBackButton: boolean, title: string) => void;
}

export const useAppStore = create<AppState>((set) => ({
  isBackButton: false,
  headerTitle: '기본 헤더',
  setHeaderInfo: (isBackButton, title) => set({ isBackButton, headerTitle: title }),
}));