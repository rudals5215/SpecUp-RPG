"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { apiClient } from "@/lib/api/client";

interface AlarmSetting {
  morningAlarm: boolean;
  morningTime: string;
  afternoonAlarm: boolean;
  deadlineAlarm: boolean;
  comebackAlarm: boolean;
  levelupAlarm: boolean;
  pushEnabled: boolean;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export default function AlarmsPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();
  const [setting, setSetting] = useState<AlarmSetting | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const fetchAlarmSetting = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiClient.get<ApiResponse<AlarmSetting>>("/api/alarms/me");
      if (res.data.success) setSetting(res.data.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!accessToken) { router.push("/login"); return; }
    fetchAlarmSetting();
  }, [accessToken, fetchAlarmSetting, router]);

  async function handleSave() {
    if (!setting) return;
    setSaving(true);
    setSuccessMessage("");
    try {
      const res = await apiClient.patch<ApiResponse<AlarmSetting>>("/api/alarms/me", setting);
      if (res.data.success) {
        setSetting(res.data.data);
        setSuccessMessage("알람 설정이 저장됐어요!");
      }
    } catch {
      // 에러 처리
    } finally {
      setSaving(false);
    }
  }

  function toggle(key: keyof AlarmSetting) {
    if (!setting) return;
    setSetting({ ...setting, [key]: !setting[key as keyof AlarmSetting] });
  }

  if (loading || !setting) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading alarms...</p>
      </div>
    );
  }

  const alarmItems = [
    { key: "morningAlarm", label: "🌅 아침 퀘스트 알람", desc: "오늘의 퀘스트 시작 알림" },
    { key: "afternoonAlarm", label: "☀️ 오후 중간 체크", desc: "미완료 퀘스트 리마인더" },
    { key: "deadlineAlarm", label: "⏰ 마감 전 알람", desc: "오늘 퀘스트 마감 1시간 전" },
    { key: "comebackAlarm", label: "🐾 복귀 알람", desc: "3일 이상 미접속 시 알림" },
    { key: "levelupAlarm", label: "⭐ 레벨업 직전 알람", desc: "XP가 거의 찼을 때 알림" },
    { key: "pushEnabled", label: "📱 푸시 알람 전체", desc: "모바일 푸시 알람 ON/OFF" },
  ] as const;

  return (
    <div className="min-h-screen bg-base p-4 md:p-6">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => router.push("/dashboard")} className="font-mono text-sm text-ink-muted hover:text-ink">
          ← 대시보드
        </button>
        <h1 className="font-bold text-ink">알람 설정</h1>
      </div>

      {successMessage && (
        <div className="mb-4 rounded-card bg-success/10 px-4 py-3 font-mono text-sm text-success">{successMessage}</div>
      )}

      <div className="flex flex-col gap-3 max-w-md">
        {/* 아침 시간 설정 */}
        {setting.morningAlarm && (
          <div className="rounded-card border border-border bg-surface p-4">
            <label className="font-mono text-xs text-ink-muted">$ morning_time</label>
            <input
              type="time"
              value={setting.morningTime}
              onChange={(e) => setSetting({ ...setting, morningTime: e.target.value })}
              className="mt-1 w-full rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink focus:border-primary focus:outline-none"
            />
          </div>
        )}

        {/* 알람 토글 목록 */}
        <div className="rounded-card border border-border bg-surface overflow-hidden">
          {alarmItems.map(({ key, label, desc }, i) => (
            <div
              key={key}
              className={`flex items-center justify-between px-4 py-3 ${
                i !== alarmItems.length - 1 ? "border-b border-border" : ""
              }`}
            >
              <div>
                <p className="text-sm font-medium text-ink">{label}</p>
                <p className="font-mono text-xs text-ink-faint">{desc}</p>
              </div>
              {/* 토글 버튼 */}
              <button
                onClick={() => toggle(key)}
                className={`relative h-6 w-11 rounded-full transition-colors ${
                  setting[key] ? "bg-primary" : "bg-border"
                }`}
              >
                <span
                  className={`absolute top-0.5 h-5 w-5 rounded-full bg-white shadow transition-transform ${
                    setting[key] ? "translate-x-5" : "translate-x-0.5"
                  }`}
                />
              </button>
            </div>
          ))}
        </div>

        <button
          onClick={handleSave}
          disabled={saving}
          className="rounded-card bg-primary py-2 font-mono text-sm text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
        >
          {saving ? "저장 중..." : "설정 저장"}
        </button>
      </div>
    </div>
  );
}
