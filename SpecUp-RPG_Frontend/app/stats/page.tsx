"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { getWeeklyStats, WeeklyStats } from "@/lib/api/stats";

const DAY_LABELS = ["월", "화", "수", "목", "금", "토", "일"];

export default function StatsPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();
  const [stats, setStats] = useState<WeeklyStats | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchStats = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getWeeklyStats();
      if (res.success) setStats(res.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!accessToken) { router.push("/login"); return; }
    fetchStats();
  }, [accessToken, fetchStats, router]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading stats...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      {/* 헤더 */}
      <div className="mb-6 flex items-center gap-3">
        <button
          onClick={() => router.push("/dashboard")}
          className="font-mono text-sm text-ink-muted hover:text-ink"
        >
          ← 대시보드
        </button>
        <h1 className="font-bold text-ink">주간 성장 통계</h1>
        {stats && (
          <span className="ml-auto font-mono text-xs text-ink-muted">
            {stats.weekStart} ~ {stats.weekEnd}
          </span>
        )}
      </div>

      {/* 요약 카드 */}
      {stats && (
        <>
          <div className="mb-4 grid grid-cols-3 gap-3">
            <div className="rounded-card border border-border bg-surface p-4 text-center">
              <p className="font-mono text-2xl font-bold text-primary">
                {stats.totalXpGained}
              </p>
              <p className="mt-1 font-mono text-xs text-ink-muted">획득 XP</p>
            </div>
            <div className="rounded-card border border-border bg-surface p-4 text-center">
              <p className="font-mono text-2xl font-bold text-gold">
                {stats.totalGoldGained}
              </p>
              <p className="mt-1 font-mono text-xs text-ink-muted">획득 골드</p>
            </div>
            <div className="rounded-card border border-border bg-surface p-4 text-center">
              <p className="font-mono text-2xl font-bold text-success">
                {stats.bestStreak}일
              </p>
              <p className="mt-1 font-mono text-xs text-ink-muted">최고 스트릭</p>
            </div>
          </div>

          {/* 주간 달성률 바 차트 */}
          <div className="rounded-card border border-border bg-surface p-4">
            <h2 className="mb-4 font-mono text-sm text-ink-muted">요일별 달성률</h2>
            <div className="flex items-end justify-around gap-2 h-40">
              {stats.dailyStats.map((day, i) => (
                <div key={day.date} className="flex flex-1 flex-col items-center gap-1">
                  {/* 바 */}
                  <div className="w-full flex flex-col justify-end h-32 bg-border rounded-sm overflow-hidden">
                    <div
                      className="w-full rounded-sm transition-all duration-500"
                      style={{
                        height: `${day.rate}%`,
                        backgroundColor: day.rate === 100
                          ? "var(--color-success)"
                          : day.rate > 50
                          ? "var(--color-primary)"
                          : day.rate > 0
                          ? "var(--color-gold)"
                          : "transparent",
                      }}
                    />
                  </div>
                  {/* 달성률 */}
                  <p className="font-mono text-xs text-ink-muted">{day.rate}%</p>
                  {/* 요일 */}
                  <p className="font-mono text-xs text-ink-faint">
                    {DAY_LABELS[i] ?? DAY_LABELS[new Date(day.date).getDay()]}
                  </p>
                </div>
              ))}
            </div>

            {/* 범례 */}
            <div className="mt-4 flex gap-4 font-mono text-xs text-ink-muted">
              <span className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-success inline-block" /> 100%
              </span>
              <span className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-primary inline-block" /> 50%+
              </span>
              <span className="flex items-center gap-1">
                <span className="h-2 w-2 rounded-full bg-gold inline-block" /> 1%+
              </span>
            </div>
          </div>

          {/* 일별 상세 */}
          <div className="mt-4 rounded-card border border-border bg-surface p-4">
            <h2 className="mb-3 font-mono text-sm text-ink-muted">일별 상세</h2>
            <div className="flex flex-col gap-2">
              {stats.dailyStats.map((day, i) => (
                <div key={day.date} className="flex items-center gap-3">
                  <span className="w-6 font-mono text-xs text-ink-faint">
                    {DAY_LABELS[i]}
                  </span>
                  <div className="flex-1 h-1.5 overflow-hidden rounded-full bg-border">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${day.rate}%`,
                        backgroundColor: day.rate === 100
                          ? "var(--color-success)"
                          : day.rate > 50
                          ? "var(--color-primary)"
                          : "var(--color-gold)",
                      }}
                    />
                  </div>
                  <span className="font-mono text-xs text-ink-muted w-16 text-right">
                    {day.completedCount}/{day.totalCount} ({day.rate}%)
                  </span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
