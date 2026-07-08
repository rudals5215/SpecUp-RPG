"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { apiClient } from "@/lib/api/client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export default function ProfilePage() {
  const router = useRouter();
  const { accessToken, user, updateUser, logout } = useAuthStore();

  const [nickname, setNickname] = useState(user?.nickname ?? "");
  const [nicknameLoading, setNicknameLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!accessToken) { router.push("/login"); return; }
    setNickname(user?.nickname ?? "");
  }, [accessToken, user, router]);

  async function handleUpdateNickname(e: React.FormEvent) {
    e.preventDefault();
    if (!nickname.trim()) return;
    setNicknameLoading(true);
    setSuccessMessage("");
    setErrorMessage("");
    try {
      const res = await apiClient.patch<ApiResponse<{ nickname: string }>>(
        "/api/users/me/nickname",
        { nickname }
      );
      if (res.data.success) {
        updateUser({ nickname: res.data.data.nickname });
        setSuccessMessage("닉네임이 변경됐어요!");
      }
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message ?? "닉네임 변경 중 문제가 생겼어요.");
    } finally {
      setNicknameLoading(false);
    }
  }

  async function handleLogout() {
    try {
      await apiClient.post("/api/auth/logout");
    } catch {
      // 실패해도 클라이언트 상태 초기화
    } finally {
      logout();
      router.push("/login");
    }
  }

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => router.push("/dashboard")} className="font-mono text-sm text-ink-muted hover:text-ink">
          ← 대시보드
        </button>
        <h1 className="font-bold text-ink">내 프로필</h1>
      </div>

      {successMessage && (
        <div className="mb-4 rounded-card bg-success/10 px-4 py-3 font-mono text-sm text-success">{successMessage}</div>
      )}
      {errorMessage && (
        <div className="mb-4 rounded-card bg-red-500/10 px-4 py-3 font-mono text-sm text-red-400">{errorMessage}</div>
      )}

      <div className="flex flex-col gap-4 max-w-md">
        {/* 기본 정보 */}
        <div className="rounded-card border border-border bg-surface p-4">
          <h2 className="mb-4 font-mono text-sm text-ink-muted">기본 정보</h2>
          <div className="flex flex-col gap-3">
            <div>
              <label className="font-mono text-xs text-ink-muted">$ email</label>
              <p className="mt-1 rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink-muted">{user?.email}</p>
            </div>
            <div>
              <label className="font-mono text-xs text-ink-muted">$ role</label>
              <p className="mt-1 rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink-muted">{user?.role}</p>
            </div>
          </div>
        </div>

        {/* 닉네임 변경 */}
        <div className="rounded-card border border-border bg-surface p-4">
          <h2 className="mb-4 font-mono text-sm text-ink-muted">닉네임 변경</h2>
          <form onSubmit={handleUpdateNickname} className="flex flex-col gap-3">
            <div>
              <label className="font-mono text-xs text-ink-muted">$ nickname</label>
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                minLength={2}
                maxLength={10}
                className="mt-1 w-full rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink focus:border-primary focus:outline-none"
              />
              <p className="mt-1 font-mono text-xs text-ink-faint">2~10자로 입력해주세요</p>
            </div>
            <button
              type="submit"
              disabled={nicknameLoading || nickname === user?.nickname}
              className="rounded-card bg-primary py-2 font-mono text-sm text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
            >
              {nicknameLoading ? "변경 중..." : "닉네임 변경"}
            </button>
          </form>
        </div>

        {/* AI 토큰 현황 */}
        <div className="rounded-card border border-border bg-surface p-4">
          <h2 className="mb-3 font-mono text-sm text-ink-muted">AI 토큰</h2>
          <div className="flex items-center justify-between">
            <p className="font-mono text-sm text-ink">
              남은 토큰: <span className="text-primary">{user?.aiToken}/{user?.aiTokenMax}</span>
            </p>
            <p className="font-mono text-xs text-ink-faint">24시간마다 자동 충전</p>
          </div>
          <div className="mt-2 h-1.5 w-full overflow-hidden rounded-full bg-border">
            <div
              className="h-full rounded-full bg-primary transition-all"
              style={{ width: `${((user?.aiToken ?? 0) / (user?.aiTokenMax ?? 10)) * 100}%` }}
            />
          </div>
        </div>

        {/* 로그아웃 */}
        <button
          onClick={handleLogout}
          className="rounded-card border border-red-500/30 py-2 font-mono text-sm text-red-400 hover:bg-red-500/10 transition-colors"
        >
          로그아웃
        </button>
      </div>
    </div>
  );
}
