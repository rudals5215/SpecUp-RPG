"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { getMyAchievements, MyAchievements } from "@/lib/api/achievement";

export default function AchievementsPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();
  const [data, setData] = useState<MyAchievements | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchAchievements = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getMyAchievements();
      if (res.success) setData(res.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!accessToken) { router.push("/login"); return; }
    fetchAchievements();
  }, [accessToken, fetchAchievements, router]);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading achievements...</p>
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
        <h1 className="font-bold text-ink">획득 칭호</h1>
        {data && (
          <span className="ml-auto font-mono text-sm text-ink-muted">
            총 {data.totalCount}개
          </span>
        )}
      </div>

      {data?.achievements.length === 0 ? (
        <div className="py-16 text-center">
          <p className="text-4xl mb-4">🏆</p>
          <p className="font-mono text-sm text-ink-faint">
            아직 획득한 칭호가 없어요
          </p>
          <p className="mt-2 font-mono text-xs text-ink-faint">
            퀘스트를 완료하면 칭호를 획득할 수 있어요!
          </p>
          <button
            onClick={() => router.push("/quests")}
            className="mt-4 font-mono text-xs text-primary hover:underline"
          >
            퀘스트 하러가기 →
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 md:grid-cols-3">
          {data?.achievements.map((achievement) => (
            <div
              key={achievement.achievementId}
              className="rounded-card border border-gold/30 bg-surface p-4"
            >
              <div className="mb-2 flex items-start gap-3">
                <span className="text-2xl">
                  {achievement.badgeImage ?? "🏅"}
                </span>
                <div>
                  <p className="font-bold text-gold">{achievement.title}</p>
                  <p className="mt-0.5 text-xs text-ink-muted">
                    {achievement.description}
                  </p>
                </div>
              </div>
              <p className="font-mono text-xs text-ink-faint">
                {new Date(achievement.earnedAt).toLocaleDateString("ko-KR")} 획득
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
