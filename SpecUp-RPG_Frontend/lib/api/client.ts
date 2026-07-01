import axios from "axios";
import { useAuthStore } from "@/store/authStore";

// 백엔드 서버 기본 주소
// 나중에 배포할 때는 .env 파일로 분리해서 관리할 거예요 (지금은 로컬 개발 단계라 직접 입력)
const BASE_URL = "http://localhost:8080";

// axios 인스턴스 생성
// 이 apiClient를 통해서만 백엔드와 통신해요
export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: 모든 API 요청을 보내기 직전에 실행돼요
// → "로그인 토큰이 있으면 헤더에 자동으로 붙여줘" 역할
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터: 모든 API 응답을 받은 직후에 실행돼요
// → 401(인증 만료) 응답이 오면 자동으로 로그아웃 처리
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout();
      // 로그인 페이지로 강제 이동
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);
