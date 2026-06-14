"use client";

import React from "react";

// 1일차 임시 가짜 데이터 (백엔드와 연결하기 전 맛보기)
const mockCharacter = {
  nickname: "임시용사",
  level: 1,
  xpPercentage: 65,
  gold: 1500,
  jobClass: "백엔드 개발자 Tier 1",
};

const mockQuests = [
  {
    id: 1,
    title: "오늘 깃허브 잔디 심기 (커밋 1회)",
    reward: "50 XP / 200 G",
    done: false,
  },
  {
    id: 2,
    title: "백엔드 기초 알고리즘 1문제 풀기",
    reward: "100 XP / 500 G",
    done: true,
  },
];

export default function Home() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 font-sans">
      <div className="max-w-5xl mx-auto space-y-6">
        {/* 헤더 타이틀 */}
        <header className="border-b border-slate-800 pb-4">
          <h1 className="text-3xl font-extrabold text-amber-500 tracking-wider">
            🛡️ SpecUp-RPG
          </h1>
          <p className="text-slate-400 text-sm">
            성장형 개발자 능력치 관리 시스템
          </p>
        </header>

        {/* 메인 레이아웃 (그리드) */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* 1. 캐릭터 스테이터스 카드 */}
          <div className="md:col-span-2 bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl space-y-4">
            <div className="flex justify-between items-center">
              <div>
                <span className="text-xs font-bold text-amber-400 uppercase tracking-widest">
                  {mockCharacter.jobClass}
                </span>
                <h2 className="text-2xl font-bold text-white">
                  {mockCharacter.nickname}
                </h2>
              </div>
              <div className="bg-amber-500/10 border border-amber-500/30 text-amber-400 px-3 py-1 rounded-full font-black">
                Lv. {mockCharacter.level}
              </div>
            </div>

            {/* 경험치 바 */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs text-slate-400">
                <span>경험치 (XP)</span>
                <span>{mockCharacter.xpPercentage}%</span>
              </div>
              <div className="w-full bg-slate-800 h-3 rounded-full overflow-hidden">
                <div
                  className="bg-gradient-to-r from-amber-500 to-orange-500 h-full transition-all duration-500"
                  style={{ width: `${mockCharacter.xpPercentage}%` }}
                ></div>
              </div>
            </div>

            {/* 골드 인벤토리 */}
            <div className="bg-slate-950 rounded-lg p-3 flex justify-between items-center border border-slate-800/50">
              <span className="text-sm text-slate-400">보유 골드</span>
              <span className="font-bold text-yellow-400">
                💰 {mockCharacter.gold.toLocaleString()} G
              </span>
            </div>
          </div>

          {/* 2. 펫 사이드바 */}
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl flex flex-col items-center justify-center text-center space-y-3">
            <div className="text-5xl animate-bounce">🦖</div>
            <h3 className="font-bold text-lg">성장 중인 펫</h3>
            <p className="text-xs text-rose-400 bg-rose-500/10 px-2 py-1 rounded border border-rose-500/20">
              "주인님, 오늘 공부는 언제 하나요? 배고파요!"
            </p>
          </div>
        </div>

        {/* 3. 하단 퀘스트 목록 */}
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 shadow-xl space-y-4">
          <h3 className="text-xl font-bold text-white flex items-center gap-2">
            🎯 오늘의 일일 퀘스트
          </h3>
          <div className="space-y-2">
            {mockQuests.map((quest) => (
              <div
                key={quest.id}
                className={`flex justify-between items-center p-4 rounded-lg border transition-all ${
                  quest.done
                    ? "bg-slate-950/40 border-emerald-500/30 text-slate-500 line-through"
                    : "bg-slate-950 border-slate-800 text-slate-200 hover:border-slate-700"
                }`}
              >
                <div className="flex items-center gap-3">
                  <input
                    type="checkbox"
                    checked={quest.done}
                    readOnly
                    className="w-4 h-4 rounded border-slate-700 bg-slate-800 text-amber-500 focus:ring-amber-500"
                  />
                  <span>{quest.title}</span>
                </div>
                <span
                  className={`text-xs px-2 py-1 rounded ${quest.done ? "bg-slate-800 text-slate-500" : "bg-amber-500/10 text-amber-400 border border-amber-500/20"}`}
                >
                  보상: {quest.reward}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
