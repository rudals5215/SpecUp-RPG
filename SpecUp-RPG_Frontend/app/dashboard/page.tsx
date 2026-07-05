"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { getDashboard, DashboardData } from "@/lib/api/dashboard";

export default function DashboardPage() {
  const router = useRouter();
  const { user, accessToken } = useAuthStore();
  const [dashboard, setDashboard] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchDashboard = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getDashboard();
      if (res.success) setDashboard(res.data);
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
    fetchDashboard();
  }, [accessToken, fetchDashboard, router]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading...</p>
      </div>
    );
  }

  if (!dashboard) return null;

  const {
    user: charInfo,
    todayQuests,
    pet,
    activeAiToken,
    aiTokenMax,
  } = dashboard;

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      {/* 상단: 캐릭터 스탯 바 */}
      <header className="mb-6 rounded-card border border-border bg-surface p-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full border-2 border-primary bg-primary/10 font-mono text-sm font-bold text-primary">
              {charInfo.level}
            </div>
            <div>
              <p className="font-bold text-ink">{charInfo.nickname}</p>
              <p className="font-mono text-xs text-ink-muted">
                {user?.role ?? "DEVELOPER"}
              </p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="font-mono text-sm text-gold">
              🪙 {charInfo.gold.toLocaleString()} G
            </div>
            <div className="font-mono text-sm text-ink-muted">
              AI {activeAiToken}/{aiTokenMax}
            </div>
          </div>
        </div>

        {/* XP 바 */}
        <div className="mt-3">
          <div className="mb-1 flex justify-between font-mono text-xs text-ink-muted">
            <span>
              EXP {charInfo.experiencePoints} / {charInfo.nextLevelXp} XP
            </span>
            <span>{charInfo.progressPercent}%</span>
          </div>
          <div className="h-2 w-full overflow-hidden rounded-full bg-border">
            <div
              className="h-full rounded-full bg-primary transition-all duration-500"
              style={{ width: `${charInfo.progressPercent}%` }}
            />
          </div>
        </div>

        {charInfo.streakDays > 0 && (
          <p className="mt-2 font-mono text-xs text-gold">
            🔥 {charInfo.streakDays}일 연속 달성 중
          </p>
        )}
      </header>

      {/* 메인 그리드 */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {/* 왼쪽: 펫 */}
        <div className="rounded-card border border-border bg-surface p-4">
          <h2 className="mb-3 text-sm font-semibold text-ink-muted">
            My Partner Pet
          </h2>
          {pet ? (
            <div
              className="flex flex-col items-center gap-2 py-4 cursor-pointer hover:opacity-80 transition-opacity"
              onClick={() => router.push("/pet")}
            >
              <div className="text-5xl">
                {pet.status === "EGG"
                  ? "🥚"
                  : pet.status === "BABY"
                    ? "🐣"
                    : pet.status === "ADULT"
                      ? "🐱"
                      : "⭐"}
              </div>
              <p className="font-bold text-ink">{pet.name}</p>
              <p className="font-mono text-xs text-ink-muted">Lv.{pet.level}</p>
              <div className="w-full">
                <div className="mb-1 flex justify-between font-mono text-xs text-ink-muted">
                  <span>배고픔</span>
                  <span>{pet.hunger}%</span>
                </div>
                <div className="h-1.5 w-full overflow-hidden rounded-full bg-border">
                  <div
                    className="h-full rounded-full bg-success transition-all duration-500"
                    style={{ width: `${pet.hunger}%` }}
                  />
                </div>
              </div>
            </div>
          ) : (
            <p className="py-8 text-center font-mono text-sm text-ink-faint">
              펫이 없어요
            </p>
          )}
        </div>

        {/* 가운데: 오늘의 퀘스트 */}
        <div className="rounded-card border border-border bg-surface p-4">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-sm font-semibold text-ink-muted">
              오늘의 일일 퀘스트
            </h2>
            <span className="font-mono text-xs text-ink-muted">
              {todayQuests.completedCount}/{todayQuests.totalCount}
            </span>
          </div>

          {/* 달성률 바 */}
          <div className="mb-4 h-1.5 w-full overflow-hidden rounded-full bg-border">
            <div
              className="h-full rounded-full bg-gold transition-all duration-500"
              style={{ width: `${todayQuests.achievementRate}%` }}
            />
          </div>

          {todayQuests.totalCount === 0 ? (
            <div className="py-6 text-center">
              <p className="font-mono text-sm text-ink-faint">
                오늘의 퀘스트가 없어요
              </p>
              <button
                onClick={() => router.push("/quests")}
                className="mt-3 font-mono text-xs text-primary hover:underline"
              >
                $ 퀘스트 추가하기
              </button>
            </div>
          ) : (
            <div className="flex flex-col gap-2">
              {/* 퀘스트 목록 */}
              {todayQuests.quests?.map((quest) => (
                <div
                  key={quest.userQuestId}
                  className={`flex items-center gap-2 rounded-card border px-3 py-2 ${
                    quest.status === "COMPLETED"
                      ? "border-success/20 opacity-50"
                      : "border-border"
                  }`}
                >
                  <span className="text-sm">
                    {quest.status === "COMPLETED" ? "✅" : "⬜"}
                  </span>
                  <span
                    className={`flex-1 text-sm ${
                      quest.status === "COMPLETED"
                        ? "line-through text-ink-muted"
                        : "text-ink"
                    }`}
                  >
                    {quest.title}
                  </span>
                  <span className="font-mono text-xs text-gold">
                    +{quest.rewardXp} XP
                  </span>
                </div>
              ))}

              <button
                onClick={() => router.push("/quests")}
                className="mt-2 w-full rounded-card border border-border py-2 font-mono text-sm text-primary transition-colors hover:bg-surface-hover"
              >
                퀘스트 페이지 →
              </button>
            </div>
          )}
        </div>

        {/* 오른쪽: 바로가기 */}
        <div className="flex flex-col gap-3 rounded-card border border-border bg-surface p-4">
          <h2 className="text-sm font-semibold text-ink-muted">바로가기</h2>
          {[
            { label: "🎒 아이템 상점", href: "/shop" },
            { label: "🏆 획득 칭호", href: "/achievements" },
            { label: "✨ AI 레벨 진단", href: "/ai-diagnosis" },
            { label: "📊 주간 통계", href: "/stats" },
            { label: "🐾 내 펫", href: "/pet" },
          ].map((item) => (
            <button
              key={item.href}
              onClick={() => router.push(item.href)}
              className="w-full rounded-card border border-border px-3 py-2 text-left font-mono text-sm text-ink-muted transition-colors hover:bg-surface-hover hover:text-ink"
            >
              {item.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
