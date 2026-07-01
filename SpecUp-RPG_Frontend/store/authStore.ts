import { create } from "zustand";
import { persist } from "zustand/middleware";

// 백엔드 로그인 응답의 user 객체와 동일한 형태로 맞춰요
export interface User {
  id: number;
  email: string;
  nickname: string;
  role: string;
  aiToken: number;
  aiTokenMax: number;
  aiTokenResetAt: string;
}

interface AuthState {
  accessToken: string | null;
  user: User | null;
  // 로그인 성공 시 토큰 + 유저 정보를 한 번에 저장
  login: (token: string, user: User) => void;
  // 로그아웃 시 전부 비움
  logout: () => void;
  // AI 토큰 차감 등 유저 정보 일부만 갱신할 때 사용
  updateUser: (user: Partial<User>) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      user: null,

      login: (token, user) => {
        set({ accessToken: token, user });
      },

      logout: () => {
        set({ accessToken: null, user: null });
      },

      updateUser: (updatedFields) => {
        const currentUser = get().user;
        if (!currentUser) return;
        set({ user: { ...currentUser, ...updatedFields } });
      },
    }),
    {
      name: "specuprpg-auth", // localStorage에 저장될 때 사용할 키 이름
    }
  )
);
