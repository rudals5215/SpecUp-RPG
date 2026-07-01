"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { login as loginApi } from "@/lib/api/auth";
import { useAuthStore } from "@/store/authStore";
import { TextField } from "@/components/ui/TextField";
import { Button } from "@/components/ui/Button";

export default function LoginPage() {
  const router = useRouter();
  const setLogin = useAuthStore((state) => state.login);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErrorMessage("");
    setLoading(true);

    try {
      const res = await loginApi({ email, password });
      // 백엔드가 success: false로 줄 수도 있어서 한번 더 체크해요
      if (!res.success) {
        setErrorMessage(res.message);
        return;
      }
      // 로그인 성공 → 토큰과 유저 정보를 전역 상태에 저장
      setLogin(res.data.accessToken, res.data.user);
      router.push("/dashboard");
    } catch (err: any) {
      // axios 에러일 경우 백엔드가 보낸 메시지를 그대로 보여줘요
      const message =
        err?.response?.data?.message ?? "로그인 중 문제가 발생했어요. 다시 시도해주세요.";
      setErrorMessage(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-sm">
        {/* 로고 영역 */}
        <div className="mb-10 text-center">
          <h1 className="font-mono text-2xl font-bold text-ink">
            Specup<span className="text-primary">RPG</span>
          </h1>
          <p className="mt-2 text-sm text-ink-muted">
            의지가 없어도 굴러가는 성장 시스템
          </p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          <TextField
            label="email"
            type="email"
            value={email}
            onChange={setEmail}
            placeholder="you@example.com"
          />
          <TextField
            label="password"
            type="password"
            value={password}
            onChange={setPassword}
            placeholder="••••••••"
          />

          {errorMessage && (
            <p className="rounded-card bg-red-500/10 px-4 py-3 text-sm text-red-400">
              {errorMessage}
            </p>
          )}

          <Button type="submit" loading={loading}>
            로그인
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-ink-muted">
          아직 계정이 없으신가요?{" "}
          <Link href="/register" className="text-primary hover:underline">
            회원가입
          </Link>
        </p>
      </div>
    </div>
  );
}
