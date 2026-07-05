import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface PetInfo {
  petId: number;
  name: string;
  petType: string;
  level: number;
  exp: number;
  hunger: number;
  status: string; // EGG / BABY / ADULT / EVOLVED
  condition: string; // GOOD / HUNGRY / STARVING
  evolvedAt: string | null;
  canEvolve: boolean;
  evolveCondition: string;
}

// 내 펫 조회
export async function getMyPet() {
  const res = await apiClient.get<ApiResponse<PetInfo>>("/api/pets/me");
  return res.data;
}

// 펫 이름 변경
export async function updatePetName(name: string) {
  const res = await apiClient.patch<ApiResponse<{ name: string }>>(
    "/api/pets/me/name",
    { name },
  );
  return res.data;
}

// AI 펫 대화
export async function chatWithPet(message: string) {
  const res = await apiClient.post<
    ApiResponse<{
      reply: string;
      petHunger: number;
      remainingAiToken: number;
    }>
  >("/api/pets/me/chat", { message });
  return res.data;
}
