import type { Metadata } from "next";
import { JetBrains_Mono } from "next/font/google";
// Pretendard는 npm 패키지로 설치해서 CSS를 그대로 불러와요
// 이미 폰트 변수(--font-pretendard 등)가 패키지 안에 정의돼 있어서
// tailwind.config.ts에서 그 이름만 그대로 가져다 쓰면 돼요
import "pretendard/dist/web/static/pretendard.css";
import "./globals.css";

const jetbrainsMono = JetBrains_Mono({
  subsets: ["latin"],
  variable: "--font-jetbrains-mono",
  weight: ["400", "500", "700"],
});

export const metadata: Metadata = {
  title: "SpecUp RPG",
  description: "포기하는 사람들을 위한, AI 맞춤형 성장 RPG 플랫폼",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className={jetbrainsMono.variable}>
      <body className="font-sans antialiased">{children}</body>
    </html>
  );
}
