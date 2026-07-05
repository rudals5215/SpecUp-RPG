"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { getMyPet, updatePetName, chatWithPet, PetInfo } from "@/lib/api/pet";

// 펫 상태별 이모지
const PET_EMOJI: Record<string, string> = {
  EGG: "🥚",
  BABY: "🐣",
  ADULT: "🐱",
  EVOLVED: "⭐",
};

// 컨디션별 색상
const CONDITION_COLOR: Record<string, string> = {
  GOOD: "text-success",
  HUNGRY: "text-gold",
  STARVING: "text-red-400",
};

export default function PetPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();

  const [pet, setPet] = useState<PetInfo | null>(null);
  const [loading, setLoading] = useState(true);

  // 이름 변경 상태
  const [editingName, setEditingName] = useState(false);
  const [newName, setNewName] = useState("");
  const [nameLoading, setNameLoading] = useState(false);

  // AI 대화 상태
  const [chatMessage, setChatMessage] = useState("");
  const [chatLog, setChatLog] = useState<{ role: "user" | "pet"; text: string }[]>([]);
  const [chatLoading, setChatLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!accessToken) {
      router.push("/login");
      return;
    }
    fetchPet();
  }, [accessToken]);

  async function fetchPet() {
    setLoading(true);
    try {
      const res = await getMyPet();
      if (res.success) {
        setPet(res.data);
        setNewName(res.data.name);
      }
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }

  // 펫 이름 변경
  async function handleUpdateName(e: React.FormEvent) {
    e.preventDefault();
    if (!newName.trim()) return;
    setNameLoading(true);
    try {
      const res = await updatePetName(newName);
      if (res.success && pet) {
        setPet({ ...pet, name: res.data.name });
        setEditingName(false);
      }
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message ?? "이름 변경 중 문제가 생겼어요.");
    } finally {
      setNameLoading(false);
    }
  }

  // AI 펫 대화
  async function handleChat(e: React.FormEvent) {
    e.preventDefault();
    if (!chatMessage.trim()) return;

    const userMsg = chatMessage;
    setChatMessage("");
    setChatLog((prev) => [...prev, { role: "user", text: userMsg }]);
    setChatLoading(true);

    try {
      const res = await chatWithPet(userMsg);
      if (res.success) {
        setChatLog((prev) => [...prev, { role: "pet", text: res.data.reply }]);
        // 펫 배고픔 수치도 업데이트
        if (pet) setPet({ ...pet, hunger: res.data.petHunger });
      }
    } catch (err: any) {
      const message = err?.response?.data?.message ?? "대화 중 문제가 생겼어요.";
      setChatLog((prev) => [...prev, { role: "pet", text: message }]);
    } finally {
      setChatLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading pet...</p>
      </div>
    );
  }

  if (!pet) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4">
        <p className="font-mono text-ink-muted">펫이 없어요</p>
        <button
          onClick={() => router.push("/dashboard")}
          className="font-mono text-sm text-primary hover:underline"
        >
          ← 대시보드로
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      {/* 상단 헤더 */}
      <div className="mb-6 flex items-center gap-3">
        <button
          onClick={() => router.push("/dashboard")}
          className="font-mono text-sm text-ink-muted hover:text-ink"
        >
          ← 대시보드
        </button>
        <h1 className="font-bold text-ink">My Partner Pet</h1>
      </div>

      {errorMessage && (
        <div className="mb-4 rounded-card bg-red-500/10 px-4 py-3 font-mono text-sm text-red-400">
          {errorMessage}
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        {/* 왼쪽: 펫 상태 카드 */}
        <div className="rounded-card border border-border bg-surface p-6">
          {/* 펫 이모지 + 이름 */}
          <div className="mb-6 flex flex-col items-center gap-3">
            <div className="text-8xl">{PET_EMOJI[pet.status] ?? "🥚"}</div>

            {/* 이름 표시/편집 */}
            {editingName ? (
              <form onSubmit={handleUpdateName} className="flex gap-2">
                <input
                  type="text"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  maxLength={10}
                  className="rounded-card border border-primary bg-base px-3 py-1.5 text-center font-bold text-ink focus:outline-none"
                  autoFocus
                />
                <button
                  type="submit"
                  disabled={nameLoading}
                  className="rounded-card bg-primary px-3 py-1.5 font-mono text-xs text-base hover:bg-primary-dim disabled:opacity-50"
                >
                  저장
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setEditingName(false);
                    setNewName(pet.name);
                  }}
                  className="rounded-card border border-border px-3 py-1.5 font-mono text-xs text-ink-muted hover:bg-surface-hover"
                >
                  취소
                </button>
              </form>
            ) : (
              <div className="flex items-center gap-2">
                <h2 className="text-xl font-bold text-ink">{pet.name}</h2>
                <button
                  onClick={() => setEditingName(true)}
                  className="font-mono text-xs text-ink-faint hover:text-ink-muted"
                >
                  ✏️
                </button>
              </div>
            )}

            <p className="font-mono text-sm text-ink-muted">
              Lv.{pet.level} · {pet.status}
            </p>
          </div>

          {/* 스탯 */}
          <div className="flex flex-col gap-3">
            {/* EXP */}
            <div>
              <div className="mb-1 flex justify-between font-mono text-xs text-ink-muted">
                <span>EXP</span>
                <span>{pet.exp} / 100</span>
              </div>
              <div className="h-1.5 w-full overflow-hidden rounded-full bg-border">
                <div
                  className="h-full rounded-full bg-primary transition-all duration-500"
                  style={{ width: `${pet.exp}%` }}
                />
              </div>
            </div>

            {/* 배고픔 */}
            <div>
              <div className="mb-1 flex justify-between font-mono text-xs text-ink-muted">
                <span>배고픔</span>
                <span className={CONDITION_COLOR[pet.condition]}>
                  {pet.hunger}% ({pet.condition})
                </span>
              </div>
              <div className="h-1.5 w-full overflow-hidden rounded-full bg-border">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${
                    pet.condition === "GOOD"
                      ? "bg-success"
                      : pet.condition === "HUNGRY"
                      ? "bg-gold"
                      : "bg-red-400"
                  }`}
                  style={{ width: `${pet.hunger}%` }}
                />
              </div>
            </div>

            {/* 진화 조건 */}
            {pet.canEvolve && (
              <div className="mt-2 rounded-card border border-primary/30 bg-primary/5 px-3 py-2">
                <p className="font-mono text-xs text-primary">{pet.evolveCondition}</p>
              </div>
            )}
          </div>
        </div>

        {/* 오른쪽: AI 펫 대화 */}
        <div className="flex flex-col rounded-card border border-border bg-surface p-4">
          <h3 className="mb-3 font-mono text-sm text-ink-muted">
            $ {pet.name}와 대화하기
          </h3>

          {/* 대화 로그 */}
          <div className="flex-1 overflow-y-auto mb-3 flex flex-col gap-2 min-h-[200px] max-h-[300px]">
            {chatLog.length === 0 ? (
              <p className="font-mono text-xs text-ink-faint py-4 text-center">
                {pet.name}에게 말을 걸어보세요!
              </p>
            ) : (
              chatLog.map((msg, i) => (
                <div
                  key={i}
                  className={`rounded-card px-3 py-2 font-mono text-sm max-w-[85%] ${
                    msg.role === "user"
                      ? "self-end bg-primary/20 text-ink"
                      : "self-start bg-surface-hover text-ink-muted"
                  }`}
                >
                  {msg.role === "pet" && (
                    <span className="mr-1 text-xs text-primary">{pet.name}:</span>
                  )}
                  {msg.text}
                </div>
              ))
            )}
            {chatLoading && (
              <div className="self-start rounded-card bg-surface-hover px-3 py-2 font-mono text-sm text-ink-faint">
                {pet.name} 답변 중...
              </div>
            )}
          </div>

          {/* 입력창 */}
          <form onSubmit={handleChat} className="flex gap-2">
            <input
              type="text"
              value={chatMessage}
              onChange={(e) => setChatMessage(e.target.value)}
              placeholder={`${pet.name}에게 말걸기...`}
              disabled={chatLoading}
              className="flex-1 rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink placeholder:text-ink-faint focus:border-primary focus:outline-none disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={chatLoading || !chatMessage.trim()}
              className="rounded-card bg-primary px-4 py-2 font-mono text-sm text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
            >
              전송
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
