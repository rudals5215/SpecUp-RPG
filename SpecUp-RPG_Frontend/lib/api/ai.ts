import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

export interface DiagnosisChatResult {
  reply: string;
  isDiagnosisComplete: boolean;
  diagnosis?: {
    diagnosisId: number;
    assessedLevel: string;
    goalSummary: string;
    dailyHours: number;
  };
  curriculum?: {
    curriculumId: number;
    title: string;
    totalWeeks: number;
  };
  remainingAiToken: number;
}

export interface CurriculumDetail {
  activeCurriculum: {
    curriculumId: number;
    title: string;
    totalWeeks: number;
    isActive: boolean;
    roadmapJson: string;
  } | null;
  pausedCurriculums: {
    curriculumId: number;
    title: string;
    resumeAt: string;
  }[];
}

// AI 진단 대화
export async function sendDiagnosisChat(
  message: string,
  conversationHistory: ChatMessage[],
) {
  const res = await apiClient.post<ApiResponse<DiagnosisChatResult>>(
    "/api/ai/diagnosis/chat",
    { message, conversationHistory },
  );
  return res.data;
}

// 커리큘럼 조회
export async function getCurriculum() {
  const res =
    await apiClient.get<ApiResponse<CurriculumDetail>>("/api/ai/curriculum");
  return res.data;
}
