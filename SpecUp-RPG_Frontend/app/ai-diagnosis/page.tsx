"use client";

import { useEffect, useState, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import {
  sendDiagnosisChat,
  getCurriculum,
  ChatMessage,
  CurriculumDetail,
} from "@/lib/api/ai";

export default function AiDiagnosisPage() {
  const router = useRouter();
  const { accessToken, user } = useAuthStore();

  const [tab, setTab] = useState<"chat" | "curriculum">("chat");
  const [curriculum, setCurriculum] = useState<CurriculumDetail | null>(null);
  const [curriculumLoading, setCurriculumLoading] = useState(false);

  // 대화 상태
  const [chatLog, setChatLog] = useState<
    { role: "user" | "ai"; text: string }[]
  >([
    {
      role: "ai",
      text: "안녕하세요! 저는 SpecUp RPG의 AI 성장 코치예요. 현재 실력과 목표를 알려주시면 맞춤 커리큘럼을 만들어드릴게요. 지금 어떤 걸 공부하고 계신가요?",
    },
  ]);
  const [conversationHistory, setConversationHistory] = useState<ChatMessage[]>(
    [],
  );
  const [inputMessage, setInputMessage] = useState("");
  const [chatLoading, setChatLoading] = useState(false);
  const [isDiagnosisComplete, setIsDiagnosisComplete] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const chatBottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!accessToken) {
      router.push("/login");
      return;
    }
  }, [accessToken, router]);

  // 대화 끝에 자동 스크롤
  useEffect(() => {
    chatBottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [chatLog]);

  // 커리큘럼 탭 진입 시 조회
  const fetchCurriculum = useCallback(async () => {
    setCurriculumLoading(true);
    try {
      const res = await getCurriculum();
      if (res.success) setCurriculum(res.data);
    } catch {
      setErrorMessage("커리큘럼을 불러오지 못했어요.");
    } finally {
      setCurriculumLoading(false);
    }
  }, []);

  useEffect(() => {
    if (tab === "curriculum") fetchCurriculum();
  }, [tab, fetchCurriculum]);

  // AI 대화 전송
  async function handleSendMessage(e: React.FormEvent) {
    e.preventDefault();
    if (!inputMessage.trim() || chatLoading) return;

    const userMsg = inputMessage.trim();
    setInputMessage("");
    setErrorMessage("");

    // 대화 로그에 유저 메시지 추가
    setChatLog((prev) => [...prev, { role: "user", text: userMsg }]);

    // 대화 이력 업데이트
    const newHistory: ChatMessage[] = [
      ...conversationHistory,
      { role: "user", content: userMsg },
    ];

    setChatLoading(true);
    try {
      const res = await sendDiagnosisChat(userMsg, newHistory);

      if (res.success) {
        const {
          reply,
          isDiagnosisComplete: complete,
          remainingAiToken,
        } = res.data;

        // AI 응답 로그에 추가
        setChatLog((prev) => [...prev, { role: "ai", text: reply }]);

        // 대화 이력에 AI 응답도 추가
        setConversationHistory([
          ...newHistory,
          { role: "assistant", content: reply },
        ]);

        if (complete) {
          setIsDiagnosisComplete(true);
          setChatLog((prev) => [
            ...prev,
            {
              role: "ai",
              text: `🎉 진단이 완료됐어요! 남은 AI 토큰: ${remainingAiToken}개. '커리큘럼' 탭에서 생성된 커리큘럼을 확인해보세요!`,
            },
          ]);
        }
      }
    } catch (err: any) {
      const message =
        err?.response?.data?.message ??
        "AI 연동 중 문제가 생겼어요. 잠시 후 다시 시도해주세요.";
      setErrorMessage(message);
      setChatLog((prev) => [...prev, { role: "ai", text: `⚠️ ${message}` }]);
    } finally {
      setChatLoading(false);
    }
  }

  // 로드맵 JSON 파싱
  function parseRoadmap(json: string) {
    try {
      return JSON.parse(json) as {
        week: number;
        title: string;
        quests: string[];
      }[];
    } catch {
      return [];
    }
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
        <h1 className="font-bold text-ink">✨ AI 레벨 진단</h1>
        {user && (
          <span className="ml-auto font-mono text-xs text-ink-muted">
            AI 토큰 {user.aiToken}/{user.aiTokenMax}
          </span>
        )}
      </div>

      {errorMessage && (
        <div className="mb-4 rounded-card bg-red-500/10 px-4 py-3 font-mono text-sm text-red-400">
          {errorMessage}
        </div>
      )}

      {/* 탭 */}
      <div className="mb-4 flex gap-2">
        {(["chat", "curriculum"] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`rounded-card px-4 py-2 font-mono text-sm transition-colors ${
              tab === t
                ? "bg-primary text-base"
                : "border border-border text-ink-muted hover:bg-surface-hover"
            }`}
          >
            {t === "chat" ? "🤖 AI 진단 대화" : "📋 내 커리큘럼"}
          </button>
        ))}
      </div>

      {/* 대화 탭 */}
      {tab === "chat" && (
        <div
          className="flex flex-col rounded-card border border-border bg-surface"
          style={{ height: "calc(100vh - 220px)" }}
        >
          {/* 대화 로그 */}
          <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-3">
            {chatLog.map((msg, i) => (
              <div
                key={i}
                className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
              >
                <div
                  className={`max-w-[80%] rounded-card px-4 py-2.5 font-mono text-sm leading-relaxed ${
                    msg.role === "user"
                      ? "bg-primary/20 text-ink"
                      : "bg-surface-hover text-ink-muted"
                  }`}
                >
                  {msg.role === "ai" && (
                    <span className="mr-1 text-xs text-primary">AI 코치:</span>
                  )}
                  {msg.text}
                </div>
              </div>
            ))}
            {chatLoading && (
              <div className="flex justify-start">
                <div className="rounded-card bg-surface-hover px-4 py-2.5 font-mono text-sm text-ink-faint">
                  AI 코치 답변 중...
                </div>
              </div>
            )}
            <div ref={chatBottomRef} />
          </div>

          {/* 입력창 */}
          <div className="border-t border-border p-3">
            {isDiagnosisComplete ? (
              <div className="text-center py-2">
                <p className="font-mono text-sm text-success mb-2">
                  ✅ 진단 완료! 커리큘럼이 생성됐어요.
                </p>
                <button
                  onClick={() => setTab("curriculum")}
                  className="rounded-card bg-primary px-4 py-2 font-mono text-sm text-base hover:bg-primary-dim transition-colors"
                >
                  커리큘럼 확인하기 →
                </button>
              </div>
            ) : (
              <form onSubmit={handleSendMessage} className="flex gap-2">
                <input
                  type="text"
                  value={inputMessage}
                  onChange={(e) => setInputMessage(e.target.value)}
                  placeholder="AI 코치에게 말걸기..."
                  disabled={chatLoading}
                  className="flex-1 rounded-card border border-border bg-base px-3 py-2 font-mono text-sm text-ink placeholder:text-ink-faint focus:border-primary focus:outline-none disabled:opacity-50"
                />
                <button
                  type="submit"
                  disabled={chatLoading || !inputMessage.trim()}
                  className="rounded-card bg-primary px-4 py-2 font-mono text-sm text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
                >
                  전송
                </button>
              </form>
            )}
          </div>
        </div>
      )}

      {/* 커리큘럼 탭 */}
      {tab === "curriculum" && (
        <>
          {curriculumLoading ? (
            <div className="flex items-center justify-center py-16">
              <p className="font-mono text-ink-muted">
                $ loading curriculum...
              </p>
            </div>
          ) : !curriculum?.activeCurriculum ? (
            <div className="py-16 text-center">
              <p className="text-4xl mb-4">📋</p>
              <p className="font-mono text-sm text-ink-faint">
                아직 커리큘럼이 없어요
              </p>
              <p className="mt-2 font-mono text-xs text-ink-faint">
                AI 진단 대화를 완료하면 맞춤 커리큘럼이 생성돼요
              </p>
              <button
                onClick={() => setTab("chat")}
                className="mt-4 font-mono text-xs text-primary hover:underline"
              >
                AI 진단 시작하기 →
              </button>
            </div>
          ) : (
            <div className="flex flex-col gap-4">
              {/* 커리큘럼 헤더 */}
              <div className="rounded-card border border-primary/30 bg-surface p-4">
                <div className="flex items-start justify-between">
                  <div>
                    <h2 className="font-bold text-ink">
                      {curriculum.activeCurriculum.title}
                    </h2>
                    <p className="mt-1 font-mono text-sm text-ink-muted">
                      총 {curriculum.activeCurriculum.totalWeeks}주 커리큘럼
                    </p>
                  </div>
                  <span className="rounded-card bg-success/10 px-2 py-1 font-mono text-xs text-success">
                    진행 중
                  </span>
                </div>
              </div>

              {/* 로드맵 */}
              <div className="flex flex-col gap-3">
                {parseRoadmap(curriculum.activeCurriculum.roadmapJson).map(
                  (week) => (
                    <div
                      key={week.week}
                      className="rounded-card border border-border bg-surface p-4"
                    >
                      <div className="mb-2 flex items-center gap-2">
                        <span className="flex h-6 w-6 items-center justify-center rounded-full bg-primary/20 font-mono text-xs text-primary">
                          {week.week}
                        </span>
                        <h3 className="font-medium text-ink">{week.title}</h3>
                      </div>
                      <ul className="flex flex-col gap-1">
                        {week.quests?.map((quest, i) => (
                          <li
                            key={i}
                            className="flex items-center gap-2 font-mono text-xs text-ink-muted"
                          >
                            <span className="text-ink-faint">$</span>
                            {quest}
                          </li>
                        ))}
                      </ul>
                    </div>
                  ),
                )}
              </div>

              {/* 일시정지된 커리큘럼 */}
              {curriculum.pausedCurriculums.length > 0 && (
                <div className="rounded-card border border-border bg-surface p-4">
                  <h3 className="mb-3 font-mono text-sm text-ink-muted">
                    일시정지 중
                  </h3>
                  {curriculum.pausedCurriculums.map((c) => (
                    <div
                      key={c.curriculumId}
                      className="flex justify-between font-mono text-sm"
                    >
                      <span className="text-ink-muted">{c.title}</span>
                      <span className="text-ink-faint">{c.resumeAt} 복귀</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
}
