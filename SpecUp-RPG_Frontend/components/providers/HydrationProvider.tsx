"use client";

import { useEffect, useState } from "react";
import { useAuthStore } from "@/store/authStore";

export default function HydrationProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [hydrated, setHydrated] = useState(false);

  useEffect(() => {
    // localStorage에서 상태 복원 후 렌더링 허용
    useAuthStore.persist.rehydrate();
    setHydrated(true);
  }, []);

  // hydration 완료 전엔 아무것도 안 그려요
  // 이렇게 해야 accessToken이 null인 상태로 페이지가 먼저 그려지지 않아요
  if (!hydrated) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-base">
        <p className="font-mono text-ink-muted">$ initializing...</p>
      </div>
    );
  }

  return <>{children}</>;
}
