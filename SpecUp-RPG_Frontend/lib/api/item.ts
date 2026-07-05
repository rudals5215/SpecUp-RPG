import { apiClient } from "./client";

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export interface ShopItem {
  itemId: number;
  name: string;
  description: string;
  itemType: string;
  jobType: string | null;
  priceGold: number;
  isPremium: boolean;
  isOwned: boolean;
}

export interface ShopData {
  myGold: number;
  items: ShopItem[];
}

export interface MyItem {
  userItemId: number;
  itemId: number;
  name: string;
  itemType: string;
  isEquipped: boolean;
  acquiredAt: string;
}

// 상점 아이템 목록 조회
export async function getShopItems(itemType?: string) {
  const params = itemType ? `?itemType=${itemType}` : "";
  const res = await apiClient.get<ApiResponse<ShopData>>(`/api/items/shop${params}`);
  return res.data;
}

// 아이템 구매
export async function purchaseItem(itemId: number) {
  const res = await apiClient.post<ApiResponse<{ itemName: string; remainingGold: number }>>(
    `/api/items/${itemId}/purchase`
  );
  return res.data;
}

// 내 아이템 목록 조회
export async function getMyItems() {
  const res = await apiClient.get<ApiResponse<MyItem[]>>("/api/items/my");
  return res.data;
}

// 아이템 장착/해제
export async function equipItem(userItemId: number, isEquipped: boolean) {
  const res = await apiClient.patch<ApiResponse<{ itemName: string; isEquipped: boolean }>>(
    `/api/items/${userItemId}/equip`,
    { isEquipped }
  );
  return res.data;
}
