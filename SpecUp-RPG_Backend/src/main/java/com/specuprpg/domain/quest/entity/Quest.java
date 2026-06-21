package com.specuprpg.domain.quest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// quest 테이블 = 퀘스트 메뉴판
// 시스템이 제공하는 기본 퀘스트 + 유저가 만든 커스텀 퀘스트가 여기 저장돼요
@Entity
@Table(name = "quest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 퀘스트 종류
    // DAILY = 매일 초기화되는 일일 퀘스트
    // WEEKLY = 주 단위 퀘스트
    // CHALLENGE = 장기 도전 과제
    @Column(nullable = false, length = 20)
    private String questType;

    // 카테고리 — 나중에 개발자 외 다른 직업군으로 확장할 때 이 컬럼만 추가하면 돼요
    // DEVELOPER / DESIGNER / EXAM_PREP / FITNESS / LANGUAGE
    @Column(nullable = false, length = 30)
    private String category;

    // 퀘스트 완료 시 지급되는 보상
    @Column(nullable = false)
    private int rewardXp;

    @Column(nullable = false)
    private int rewardGold;

    // true = 시스템이 제공하는 기본 퀘스트 (유저가 삭제 불가)
    // false = 유저가 직접 만든 커스텀 퀘스트
    @Column(nullable = false)
    private boolean isTemplate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 메서드 ───────────────────────────────────────
    // 유저가 커스텀 퀘스트를 만들 때 사용
    public static Quest createCustom(String title, String description,
                                     String questType, String category,
                                     int rewardXp, int rewardGold) {
        Quest quest = new Quest();
        quest.title = title;
        quest.description = description;
        quest.questType = questType;
        quest.category = category;
        quest.rewardXp = rewardXp;
        quest.rewardGold = rewardGold;
        quest.isTemplate = false; // 커스텀 퀘스트는 항상 false
        return quest;
    }
}
