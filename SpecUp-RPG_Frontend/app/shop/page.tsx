"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import {
  getShopItems,
  purchaseItem,
  getMyItems,
  equipItem,
  ShopData,
  MyItem,
} from "@/lib/api/item";

const ITEM_TYPE_LABELS: Record<string, string> = {
  ALL: "전체",
  EQUIPMENT: "장비",
  COSTUME: "코스튬",
  THEME: "테마",
};

export default function ShopPage() {
  const router = useRouter();
  const { accessToken } = useAuthStore();

  const [tab, setTab] = useState<"shop" | "my">("shop");
  const [filterType, setFilterType] = useState("ALL");
  const [shopData, setShopData] = useState<ShopData | null>(null);
  const [myItems, setMyItems] = useState<MyItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [processingId, setProcessingId] = useState<number | null>(null);

  const fetchShop = useCallback(async () => {
    setLoading(true);
    try {
      const type = filterType === "ALL" ? undefined : filterType;
      const res = await getShopItems(type);
      if (res.success) setShopData(res.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [filterType, router]);

  const fetchMyItems = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getMyItems();
      if (res.success) setMyItems(res.data);
    } catch {
      router.push("/login");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!accessToken) { router.push("/login"); return; }
    if (tab === "shop") fetchShop();
    else fetchMyItems();
  }, [accessToken, tab, fetchShop, fetchMyItems, router]);

  // 필터 바뀌면 상점 다시 로드
  useEffect(() => {
    if (tab === "shop") fetchShop();
  }, [filterType, fetchShop, tab]);

  // 아이템 구매
  async function handlePurchase(itemId: number) {
    setProcessingId(itemId);
    setSuccessMessage("");
    setErrorMessage("");
    try {
      const res = await purchaseItem(itemId);
      if (res.success) {
        setSuccessMessage(`${res.data.itemName} 구매 완료! 남은 골드: ${res.data.remainingGold} G`);
        fetchShop();
      }
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message ?? "구매 중 문제가 생겼어요.");
    } finally {
      setProcessingId(null);
    }
  }

  // 장착/해제
  async function handleEquip(userItemId: number, currentlyEquipped: boolean) {
    setProcessingId(userItemId);
    setSuccessMessage("");
    setErrorMessage("");
    try {
      const res = await equipItem(userItemId, !currentlyEquipped);
      if (res.success) {
        setSuccessMessage(res.data.isEquipped ? "장착했어요!" : "해제했어요.");
        fetchMyItems();
      }
    } catch (err: any) {
      setErrorMessage(err?.response?.data?.message ?? "처리 중 문제가 생겼어요.");
    } finally {
      setProcessingId(null);
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="font-mono text-ink-muted">$ loading shop...</p>
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
        <h1 className="font-bold text-ink">아이템 상점</h1>
        {shopData && (
          <span className="ml-auto font-mono text-sm text-gold">
            🪙 {shopData.myGold.toLocaleString()} G
          </span>
        )}
      </div>

      {/* 피드백 */}
      {successMessage && (
        <div className="mb-4 rounded-card bg-success/10 px-4 py-3 font-mono text-sm text-success">
          {successMessage}
        </div>
      )}
      {errorMessage && (
        <div className="mb-4 rounded-card bg-red-500/10 px-4 py-3 font-mono text-sm text-red-400">
          {errorMessage}
        </div>
      )}

      {/* 탭 */}
      <div className="mb-4 flex gap-2">
        {(["shop", "my"] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`rounded-card px-4 py-2 font-mono text-sm transition-colors ${
              tab === t
                ? "bg-primary text-base"
                : "border border-border text-ink-muted hover:bg-surface-hover"
            }`}
          >
            {t === "shop" ? "🛒 상점" : "🎒 내 아이템"}
          </button>
        ))}
      </div>

      {/* 상점 탭 */}
      {tab === "shop" && (
        <>
          {/* 필터 */}
          <div className="mb-4 flex gap-2 flex-wrap">
            {Object.entries(ITEM_TYPE_LABELS).map(([key, label]) => (
              <button
                key={key}
                onClick={() => setFilterType(key)}
                className={`rounded-card px-3 py-1.5 font-mono text-xs transition-colors ${
                  filterType === key
                    ? "bg-primary/20 text-primary border border-primary/50"
                    : "border border-border text-ink-muted hover:bg-surface-hover"
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* 아이템 목록 */}
          {shopData?.items.length === 0 ? (
            <p className="py-16 text-center font-mono text-sm text-ink-faint">
              아이템이 없어요
            </p>
          ) : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 md:grid-cols-3">
              {shopData?.items.map((item) => (
                <div
                  key={item.itemId}
                  className={`rounded-card border bg-surface p-4 ${
                    item.isOwned ? "border-success/30 opacity-70" : "border-border"
                  }`}
                >
                  <div className="mb-2 flex items-start justify-between gap-2">
                    <div>
                      <p className="font-medium text-ink">{item.name}</p>
                      <p className="mt-0.5 font-mono text-xs text-ink-muted">
                        {item.itemType}
                        {item.jobType && ` · ${item.jobType}`}
                        {item.isPremium && " · ⭐ 프리미엄"}
                      </p>
                    </div>
                    <span className="shrink-0 font-mono text-sm text-gold">
                      {item.priceGold} G
                    </span>
                  </div>

                  {item.description && (
                    <p className="mb-3 text-xs text-ink-muted">{item.description}</p>
                  )}

                  {item.isOwned ? (
                    <p className="font-mono text-xs text-success">✓ 보유 중</p>
                  ) : (
                    <button
                      onClick={() => handlePurchase(item.itemId)}
                      disabled={processingId === item.itemId}
                      className="w-full rounded-card bg-primary py-1.5 font-mono text-xs text-base hover:bg-primary-dim disabled:opacity-50 transition-colors"
                    >
                      {processingId === item.itemId ? "구매 중..." : "구매하기"}
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {/* 내 아이템 탭 */}
      {tab === "my" && (
        <>
          {myItems.length === 0 ? (
            <div className="py-16 text-center">
              <p className="font-mono text-sm text-ink-faint">보유한 아이템이 없어요</p>
              <button
                onClick={() => setTab("shop")}
                className="mt-3 font-mono text-xs text-primary hover:underline"
              >
                상점 둘러보기 →
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 md:grid-cols-3">
              {myItems.map((item) => (
                <div
                  key={item.userItemId}
                  className={`rounded-card border bg-surface p-4 ${
                    item.isEquipped ? "border-primary/50" : "border-border"
                  }`}
                >
                  <div className="mb-3 flex items-start justify-between">
                    <div>
                      <p className="font-medium text-ink">{item.name}</p>
                      <p className="mt-0.5 font-mono text-xs text-ink-muted">
                        {item.itemType}
                      </p>
                    </div>
                    {item.isEquipped && (
                      <span className="font-mono text-xs text-primary">장착 중</span>
                    )}
                  </div>
                  <button
                    onClick={() => handleEquip(item.userItemId, item.isEquipped)}
                    disabled={processingId === item.userItemId}
                    className={`w-full rounded-card py-1.5 font-mono text-xs transition-colors disabled:opacity-50 ${
                      item.isEquipped
                        ? "border border-border text-ink-muted hover:bg-surface-hover"
                        : "bg-primary text-base hover:bg-primary-dim"
                    }`}
                  >
                    {processingId === item.userItemId
                      ? "처리 중..."
                      : item.isEquipped
                      ? "해제하기"
                      : "장착하기"}
                  </button>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}
