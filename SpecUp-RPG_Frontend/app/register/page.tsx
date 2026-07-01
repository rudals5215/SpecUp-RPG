"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { register as registerApi } from "@/lib/api/auth";
import { TextField } from "@/components/ui/TextField";
import { Button } from "@/components/ui/Button";

export default function RegisterPage() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");
  const [nickname, setNickname] = useState("");

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  // 비밀번호 확인이 다를 때만 즉시 알려줘요 (입력 중 실시간 피드백)
  const passwordMismatch =
    passwordCheck.length > 0 && password !== passwordCheck;

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErrorMessage("");

    if (password !== passwordCheck) {
      setErrorMessage("비밀번호가 일치하지 않아요.");
      return;
    }

    setLoading(true);
    try {
      const res = await registerApi({ email, password, nickname });
      if (!res.success) {
        setErrorMessage(res.message);
        return;
      }
      // 회원가입 성공 → 바로 로그인 페이지로 이동
      // (백엔드가 회원가입 시 토큰을 안 주니까, 로그인은 따로 한 번 더 해야 해요)
      router.push("/login?registered=true");
    } catch (err: any) {
      const message =
        err?.response?.data?.message ?? "회원가입 중 문제가 발생했어요. 다시 시도해주세요.";
      setErrorMessage(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="mb-10 text-center">
          <h1 className="font-mono text-2xl font-bold text-ink">
            Specup<span className="text-primary">RPG</span>
          </h1>
          <p className="mt-2 text-sm text-ink-muted">
            첫 캐릭터를 생성해볼까요?
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
            label="nickname"
            value={nickname}
            onChange={setNickname}
            placeholder="2~10자로 입력해주세요"
          />
          <TextField
            label="password"
            type="password"
            value={password}
            onChange={setPassword}
            placeholder="8자 이상 입력해주세요"
          />
          <TextField
            label="password_check"
            type="password"
            value={passwordCheck}
            onChange={setPasswordCheck}
            placeholder="비밀번호를 한 번 더 입력해주세요"
            error={passwordMismatch ? "비밀번호가 일치하지 않아요." : undefined}
          />

          {errorMessage && (
            <p className="rounded-card bg-red-500/10 px-4 py-3 text-sm text-red-400">
              {errorMessage}
            </p>
          )}

          <Button type="submit" loading={loading}>
            캐릭터 생성하기
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-ink-muted">
          이미 계정이 있으신가요?{" "}
          <Link href="/login" className="text-primary hover:underline">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
