package com.specuprpg.domain.item.dto;

import com.specuprpg.domain.item.entity.Item;
import com.specuprpg.domain.item.entity.UserItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDto {

    // 상점 아이템 목록 응답
    @Getter
    @Builder
    public static class ShopItem {
        private Long itemId;
        private String name;
        private String description;
        private String itemType;
        private String jobType;
        private int priceGold;
        private boolean isPremium;
        private boolean isOwned;    // 이미 보유 중인지

        public static ShopItem of(Item item, boolean isOwned) {
            return ShopItem.builder()
                    .itemId(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .itemType(item.getItemType())
                    .jobType(item.getJobType())
                    .priceGold(item.getPriceGold())
                    .isPremium(item.isPremium())
                    .isOwned(isOwned)
                    .build();
        }
    }

    // 상점 목록 조회 응답
    @Getter
    @Builder
    public static class ShopList {
        private int myGold;
        private List<ShopItem> items;
    }

    // 아이템 구매 응답
    @Getter
    @Builder
    public static class PurchaseResult {
        private String itemName;
        private int remainingGold;
    }

    // 내 아이템 목록 응답
    @Getter
    @Builder
    public static class MyItem {
        private Long userItemId;
        private Long itemId;
        private String name;
        private String itemType;
        private boolean isEquipped;
        private LocalDateTime acquiredAt;

        public static MyItem from(UserItem userItem) {
            return MyItem.builder()
                    .userItemId(userItem.getId())
                    .itemId(userItem.getItem().getId())
                    .name(userItem.getItem().getName())
                    .itemType(userItem.getItem().getItemType())
                    .isEquipped(userItem.isEquipped())
                    .acquiredAt(userItem.getAcquiredAt())
                    .build();
        }
    }

    // 장착/해제 응답
    @Getter
    @Builder
    public static class EquipResult {
        private String itemName;
        private boolean isEquipped;
    }
}
