package com.specuprpg.domain.item.service;

import com.specuprpg.domain.item.dto.ItemResponseDto;
import com.specuprpg.domain.item.entity.Item;
import com.specuprpg.domain.item.entity.UserItem;
import com.specuprpg.domain.item.repository.ItemRepository;
import com.specuprpg.domain.item.repository.UserItemRepository;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // ── 상점 아이템 목록 조회 ─────────────────────────────
    @Transactional(readOnly = true)
    public ItemResponseDto.ShopList getShopItems(Long userId, String itemType) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 전체 아이템 조회 (itemType 필터 적용)
        List<Item> items = itemType != null
                ? itemRepository.findByItemType(itemType)
                : itemRepository.findAll();

        // 유저가 보유한 아이템 ID 목록
        Set<Long> ownedItemIds = userItemRepository.findAllByUserId(userId)
                .stream()
                .map(ui -> ui.getItem().getId())
                .collect(Collectors.toSet());

        List<ItemResponseDto.ShopItem> shopItems = items.stream()
                .map(item -> ItemResponseDto.ShopItem.of(item, ownedItemIds.contains(item.getId())))
                .toList();

        return ItemResponseDto.ShopList.builder()
                .myGold(status.getGold())
                .items(shopItems)
                .build();
    }

    // ── 아이템 구매 ───────────────────────────────────────
    @Transactional
    public ItemResponseDto.PurchaseResult purchaseItem(Long userId, Long itemId) {

        // 1. 아이템 존재 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // 2. 이미 보유한 아이템인지 확인
        if (userItemRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new CustomException(ErrorCode.ITEM_ALREADY_OWNED);
        }

        // 3. 골드 충분한지 확인 후 차감
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // useGold가 false면 골드 부족
        if (!status.useGold(item.getPriceGold())) {
            throw new CustomException(ErrorCode.GOLD_INSUFFICIENT);
        }

        // 4. 유저 아이템 목록에 추가
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserItem userItem = UserItem.create(user, item);
        userItemRepository.save(userItem);

        log.info("[아이템 구매] userId={}, itemId={}, itemName={}, gold-={}",
                userId, itemId, item.getName(), item.getPriceGold());

        return ItemResponseDto.PurchaseResult.builder()
                .itemName(item.getName())
                .remainingGold(status.getGold())
                .build();
    }

    // ── 내 아이템 목록 조회 ───────────────────────────────
    @Transactional(readOnly = true)
    public List<ItemResponseDto.MyItem> getMyItems(Long userId) {
        return userItemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemResponseDto.MyItem::from)
                .toList();
    }

    // ── 아이템 장착 / 해제 ────────────────────────────────
    @Transactional
    public ItemResponseDto.EquipResult equipItem(Long userId, Long userItemId, boolean equip) {

        UserItem userItem = userItemRepository.findById(userItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_OWNED));

        // 내 아이템인지 확인
        if (!userItem.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (equip) {
            userItem.equip();
        } else {
            userItem.unequip();
        }

        log.info("[아이템 장착] userId={}, userItemId={}, equipped={}",
                userId, userItemId, equip);

        return ItemResponseDto.EquipResult.builder()
                .itemName(userItem.getItem().getName())
                .isEquipped(equip)
                .build();
    }
}
