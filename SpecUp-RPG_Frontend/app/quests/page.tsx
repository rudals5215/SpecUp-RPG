"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import {
  getTodayQuests,
  completeQuest,
  createQuest,
  deleteQuest,
  TodayQuestsData,
  QuestInfo,
} from "@/lib/api/quest";

export default function QuestsPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();

  const [data, setData] = useState<TodayQuestsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [completingId, setCompletingId] = useState<number | null>(null);
  const [showAddForm, setShowAddForm] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  // 퀘스트 추가 폼 상태
  const [newTitle, setNewTitle] = useState("");
  const [newXp, setNewXp] = useState(30);
  const [newGold, setNewGold] = useState(10);
  const [addLoading, setAddLoading] = useState(false);

  const fetchQuests = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getTodayQuests();
      if (res.success) setData(res.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!accessToken) {
      router.push("/login");
      return;
    }
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchQuests();
  }, [accessToken, fetchQuests, router]);

  // 퀘스트 완료 버튼 클릭
  async function handleComplete(userQuestId: number) {
    setCompletingId(userQuestId);
    setErrorMessage("");
    setSuccessMessage("");
    try {
      const res = await completeQuest(userQuestId);
      if (res.success) {
        const { xpGained, goldGained, userStatus } = res.data;
        const levelUpMsg = userStatus.isLevelUp
          ? ` 🎊 레벨업! Lv.${userStatus.previousLevel} → Lv.${userStatus.level}`
          : "";
        setSuccessMessage(
          `퀘스트 완료! +${xpGained} XP, +${goldGained} G${levelUpMsg}`,
        );
        // 완료 후 목록 새로고침
        await fetchQuests();
      }
    } catch (err: any) {
      const message =
        err?.response?.data?.message ?? "퀘스트 완료 중 문제가 생겼어요.";
      setErrorMessage(message);
    } finally {
      setCompletingId(null);
    }
  }

  // 퀘스트 삭제
  async function handleDelete(userQuestId: number) {
    if (!confirm("이 퀘스트를 삭제할까요?")) return;
    try {
      await deleteQuest(userQuestId);
      await fetchQuests();
    } catch (err: any) {
      setErrorMessage(
        err?.response?.data?.message ?? "삭제 중 문제가 생겼어요.",
      );
    }
  }

  // 퀘스트 추가
  async function handleAddQuest(e: React.FormEvent) {
    e.preventDefault();
    if (!newTitle.trim()) return;
    setAddLoading(true);
    try {
      await createQuest({
        title: newTitle,
        questType: "DAILY",
        category: "DEVELOPER",
        rewardXp: newXp,
        rewardGold: newGold,
      });
      setNewTitle("");
      setNewXp(30);
      setNewGold(10);
      setShowAddForm(false);
      await fetchQuests();
    } catch (err: any) {
      setErrorMessage(
        err?.response?.data?.message ?? "퀘스트 추가 중 문제가 생겼어요.",
      );
    } finally {
      setAddLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading quests...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      {/* 상단 헤더 */}
      <div className="mb-6 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button
            onClick={() => router.push("/dashboard")}
            className="font-mono text-sm text-ink-muted hover:text-ink"
          >
            ← 대시보드
          </button>
          <h1 className="font-bold text-ink">오늘의 퀘스트</h1>
        </div>
        <button
          onClick={() => setShowAddForm(!showAddForm)}
          className="rounded-card border border-primary px-3 py-1.5 font-mono text-sm text-primary hover:bg-primary/10 transition-colors"
        >
          + 퀘스트 추가
        </button>
      </div>

      {/* 피드백 메시지 */}
      {successMessage && (
        <div className="mb-4 rounded-card bg-success/10 px-4 py-3 font-mono text-sm text-success">
          {successMessage}
        </div>
      )}
      {errorMessage && (
        <div className="mb-4 rounded-card bg-red-500/10 px-4 py-3 font-mono text-sm text-red-400">
          {errorMessage}
        </div>
      )}

      {/* 퀘스트 추가 폼 */}
      {showAddForm && (
        <form
          onSubmit={handleAddQuest}
          className="mb-6 rounded-card border border-border bg-surface p-4"
        >
          <p className="mb-3 font-mono text-sm text-primary">
            $ 새 퀘스트 추가
          </p>
          <div className="flex flex-col gap-3">
            <input
              type="text"
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              placeholder="퀘스트 이름을 입력하세요"
              className="rounded-card border border-border bg-base px-3 py-2 text-sm text-ink placeholder:text-ink-faint focus:border-primary focus:outline-none"
              required
            />
            <div className="flex gap-3">
              <div className="flex-1">
                <label className="mb-1 block font-mono text-xs text-ink-muted">
                  보상 XP
                </label>
                <input
                  type="number"
                  value={newXp}
                  onChange={(e) => setNewXp(Number(e.target.value))}
                  min={0}
                  className="w-full rounded-card border border-border bg-base px-3 py-2 text-sm text-ink focus:border-primary focus:outline-none"
                />
              </div>
              <div className="flex-1">
                <label className="mb-1 block font-mono text-xs text-ink-muted">
                  보상 골드
                </label>
                <input
                  type="number"
                  value={newGold}
                  onChange={(e) => setNewGold(Number(e.target.value))}
                  min={0}
                  className="w-full rounded-card border border-border bg-base px-3 py-2 text-sm text-ink focus:border-primary focus:outline-none"
                />
              </div>
            </div>
            <div className="flex gap-2">
              <button
                type="submit"
                disabled={addLoading}
                className="flex-1 rounded-card bg-primary py-2 font-mono text-sm text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
              >
                {addLoading ? "추가 중..." : "추가하기"}
              </button>
              <button
                type="button"
                onClick={() => setShowAddForm(false)}
                className="rounded-card border border-border px-4 py-2 font-mono text-sm text-ink-muted hover:bg-surface-hover transition-colors"
              >
                취소
              </button>
            </div>
          </div>
        </form>
      )}

      {/* 달성률 요약 */}
      {data && (
        <div className="mb-4 rounded-card border border-border bg-surface px-4 py-3">
          <div className="mb-2 flex justify-between font-mono text-sm">
            <span className="text-ink-muted">오늘 달성률</span>
            <span className="text-gold">
              {data.completedCount}/{data.totalCount} ({data.achievementRate}%)
            </span>
          </div>
          <div className="h-1.5 w-full overflow-hidden rounded-full bg-border">
            <div
              className="h-full rounded-full bg-gold transition-all duration-500"
              style={{ width: `${data.achievementRate}%` }}
            />
          </div>
        </div>
      )}

      {/* 퀘스트 목록 */}
      {data?.quests.length === 0 ? (
        <div className="py-16 text-center">
          <p className="font-mono text-ink-faint">오늘의 퀘스트가 없어요</p>
          <p className="mt-2 font-mono text-xs text-ink-faint">
            + 퀘스트 추가 버튼으로 직접 만들거나, 자정이 지나면 자동으로
            할당돼요
          </p>
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {data?.quests.map((quest: QuestInfo) => (
            <QuestCard
              key={quest.userQuestId}
              quest={quest}
              onComplete={() => handleComplete(quest.userQuestId)}
              onDelete={() => handleDelete(quest.userQuestId)}
              isCompleting={completingId === quest.userQuestId}
            />
          ))}
        </div>
      )}
    </div>
  );
}

// ── 퀘스트 카드 컴포넌트 ────────────────────────────────
function QuestCard({
  quest,
  onComplete,
  onDelete,
  isCompleting,
}: {
  quest: QuestInfo;
  onComplete: () => void;
  onDelete: () => void;
  isCompleting: boolean;
}) {
  const isCompleted = quest.status === "COMPLETED";
  const isExpired = quest.status === "EXPIRED";

  return (
    <div
      className={`rounded-card border bg-surface p-4 transition-colors ${
        isCompleted
          ? "border-success/30 opacity-60"
          : isExpired
            ? "border-border opacity-40"
            : "border-border hover:border-primary/50"
      }`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-3">
          {/* 완료 여부 아이콘 */}
          <span className="mt-0.5 text-lg">
            {isCompleted ? "✅" : isExpired ? "⏰" : "⬜"}
          </span>
          <div>
            <p
              className={`font-medium ${isCompleted ? "line-through text-ink-muted" : "text-ink"}`}
            >
              {quest.title}
            </p>
            <p className="mt-1 font-mono text-xs text-gold">
              +{quest.rewardXp} XP / +{quest.rewardGold} G
            </p>
          </div>
        </div>

        {/* 버튼 영역 */}
        <div className="flex shrink-0 gap-2">
          {!isCompleted && !isExpired && (
            <button
              onClick={onComplete}
              disabled={isCompleting}
              className="rounded-card bg-primary px-3 py-1.5 font-mono text-xs text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
            >
              {isCompleting ? "..." : "완료"}
            </button>
          )}
          {/* 시스템 퀘스트가 아닐 때만 삭제 버튼 표시 */}
          <button
            onClick={onDelete}
            className="rounded-card border border-border px-2 py-1.5 font-mono text-xs text-ink-faint hover:border-red-500/50 hover:text-red-400 transition-colors"
          >
            ✕
          </button>
        </div>
      </div>
    </div>
  );
}
