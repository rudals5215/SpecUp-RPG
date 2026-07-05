import type { Metadata } from "next";
import { JetBrains_Mono } from "next/font/google";
import "pretendard/dist/web/static/pretendard.css";
import "./globals.css";
import HydrationProvider from "@/components/providers/HydrationProvider";

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
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko" className={jetbrainsMono.variable}>
      <body className="font-sans antialiased">
        <HydrationProvider>{children}</HydrationProvider>
      </body>
    </html>
  );
}
