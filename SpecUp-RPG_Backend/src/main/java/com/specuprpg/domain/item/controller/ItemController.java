package com.specuprpg.domain.item.controller;

import com.specuprpg.domain.item.dto.ItemResponseDto;
import com.specuprpg.domain.item.service.ItemService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final JwtProvider jwtProvider;

    // ── 상점 아이템 목록 조회 ─────────────────────────────
    // GET /api/items/shop?itemType=COSTUME
    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ItemResponseDto.ShopList>> getShopItems(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String itemType) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                itemService.getShopItems(userId, itemType)));
    }

    // ── 아이템 구매 ───────────────────────────────────────
    // POST /api/items/{itemId}/purchase
    @PostMapping("/{itemId}/purchase")
    public ResponseEntity<ApiResponse<ItemResponseDto.PurchaseResult>> purchaseItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long itemId) {

        Long userId = extractUserId(token);
        ItemResponseDto.PurchaseResult response = itemService.purchaseItem(userId, itemId);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "구매 완료! 골드가 " + (response.getRemainingGold()) + " 남았어요."));
    }

    // ── 내 아이템 목록 조회 ───────────────────────────────
    // GET /api/items/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ItemResponseDto.MyItem>>> getMyItems(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(itemService.getMyItems(userId)));
    }

    // ── 아이템 장착 / 해제 ────────────────────────────────
    // PATCH /api/items/{userItemId}/equip
    @PatchMapping("/{userItemId}/equip")
    public ResponseEntity<ApiResponse<ItemResponseDto.EquipResult>> equipItem(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userItemId,
            @RequestBody Map<String, Boolean> body) {

        Long userId = extractUserId(token);
        boolean equip = body.getOrDefault("isEquipped", true);

        ItemResponseDto.EquipResult response = itemService.equipItem(userId, userItemId, equip);
        String message = equip ? "아이템을 장착했어요!" : "아이템을 해제했어요.";

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
