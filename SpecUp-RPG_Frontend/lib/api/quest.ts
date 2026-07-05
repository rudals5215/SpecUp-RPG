import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

// 퀘스트 하나의 타입
export interface QuestInfo {
  userQuestId: number;
  questId: number;
  title: string;
  description: string;
  questType: string;
  category: string;
  rewardXp: number;
  rewardGold: number;
  status: string; // ASSIGNED / COMPLETED / EXPIRED
  dueDate: string;
}

// 오늘의 퀘스트 목록 응답 타입
export interface TodayQuestsData {
  date: string;
  totalCount: number;
  completedCount: number;
  achievementRate: number;
  quests: QuestInfo[];
}

// 퀘스트 완료 응답 타입
export interface CompleteResult {
  questTitle: string;
  xpGained: number;
  goldGained: number;
  userStatus: {
    level: number;
    experiencePoints: number;
    gold: number;
    streakDays: number;
    isLevelUp: boolean;
    previousLevel: number | null;
  };
  newAchievements: string[];
}

// ── API 호출 함수들 ───────────────────────────────────

// 오늘의 퀘스트 목록 조회
export async function getTodayQuests() {
  const res =
    await apiClient.get<ApiResponse<TodayQuestsData>>("/api/quests/today");
  return res.data;
}

// 퀘스트 완료 처리
export async function completeQuest(userQuestId: number) {
  const res = await apiClient.post<ApiResponse<CompleteResult>>(
    `/api/quests/${userQuestId}/complete`,
  );
  return res.data;
}

// 커스텀 퀘스트 생성
export interface CreateQuestRequest {
  title: string;
  description?: string;
  questType: string;
  category: string;
  rewardXp: number;
  rewardGold: number;
}

export async function createQuest(payload: CreateQuestRequest) {
  const res = await apiClient.post<
    ApiResponse<{ userQuestId: number; title: string; status: string }>
  >("/api/quests", payload);
  return res.data;
}

// 퀘스트 삭제
export async function deleteQuest(userQuestId: number) {
  const res = await apiClient.delete<ApiResponse<null>>(
    `/api/quests/${userQuestId}`,
  );
  return res.data;
}
