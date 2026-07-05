import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

// GET /api/dashboard 응답 타입
export interface DashboardData {
  user: {
    nickname: string;
    level: number;
    experiencePoints: number;
    nextLevelXp: number;
    progressPercent: number;
    gold: number;
    streakDays: number;
  };
  todayQuests: {
    totalCount: number;
    completedCount: number;
    achievementRate: number;
    quests: {
      // 추가
      userQuestId: number;
      title: string;
      status: string;
      rewardXp: number;
      rewardGold: number;
    }[];
  };
  pet: {
    name: string;
    status: string;
    hunger: number;
    level: number;
  } | null;
  activeAiToken: number;
  aiTokenMax: number;
}

export async function getDashboard() {
  const res = await apiClient.get<ApiResponse<DashboardData>>("/api/dashboard");
  return res.data;
}
