import { apiClient } from "./client";
import { User } from "@/store/authStore";

// 백엔드 공통 응답 형식: { success, data, message }
interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

// ── 회원가입 ──────────────────────────────────────────
export interface RegisterRequest {
  email: string;
  password: string;
  nickname: string;
}

interface RegisterData {
  id: number;
  email: string;
  nickname: string;
  createdAt: string;
}

export async function register(payload: RegisterRequest) {
  const res = await apiClient.post<ApiResponse<RegisterData>>(
    "/api/auth/register",
    payload
  );
  return res.data; // { success, data, message }
}

// ── 로그인 ────────────────────────────────────────────
export interface LoginRequest {
  email: string;
  password: string;
}

interface LoginData {
  accessToken: string;
  tokenType: string;
  user: User;
}

export async function login(payload: LoginRequest) {
  const res = await apiClient.post<ApiResponse<LoginData>>(
    "/api/auth/login",
    payload
  );
  return res.data; // { success, data, message }
}
