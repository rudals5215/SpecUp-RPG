import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface AchievementInfo {
  achievementId: number;
  title: string;
  description: string;
  badgeImage: string | null;
  earnedAt: string;
}

export interface MyAchievements {
  totalCount: number;
  achievements: AchievementInfo[];
}

// 내 칭호 목록 조회
export async function getMyAchievements() {
  const res = await apiClient.get<ApiResponse<MyAchievements>>("/api/achievements/me");
  return res.data;
}
