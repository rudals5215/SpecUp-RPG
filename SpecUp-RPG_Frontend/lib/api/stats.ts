import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface DailyStat {
  date: string;
  completedCount: number;
  totalCount: number;
  rate: number;
}

export interface WeeklyStats {
  weekStart: string;
  weekEnd: string;
  dailyStats: DailyStat[];
  totalXpGained: number;
  totalGoldGained: number;
  bestStreak: number;
}

// 주간 통계 조회
export async function getWeeklyStats() {
  const res = await apiClient.get<ApiResponse<WeeklyStats>>("/api/stats/weekly");
  return res.data;
}
