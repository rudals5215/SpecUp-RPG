package com.specuprpg.domain.item.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// item 테이블 = 아이템 메뉴판
// 상점에서 살 수 있는 아이템 목록이 여기 저장돼요
@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    // 아이템 종류
    // EQUIPMENT(장비) / COSTUME(코스튬) / THEME(프로필 테마) / EMOTE(이모트)
    @Column(nullable = false, length = 30)
    private String itemType;

    // 직업별 전용 아이템 (null이면 공용)
    // DEVELOPER / DESIGNER / EXAM_PREP 등
    @Column(length = 50)
    private String jobType;

    // 구매에 필요한 골드
    @Column(nullable = false)
    private int priceGold;

    // 프리미엄 유저 전용 아이템 여부
    @Column(nullable = false)
    private boolean isPremium;

    @Column(length = 255)
    private String imageUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static Item create(String name, String description, String itemType,
                               String jobType, int priceGold, boolean isPremium) {
        Item item = new Item();
        item.name = name;
        item.description = description;
        item.itemType = itemType;
        item.jobType = jobType;
        item.priceGold = priceGold;
        item.isPremium = isPremium;
        return item;
    }
}
