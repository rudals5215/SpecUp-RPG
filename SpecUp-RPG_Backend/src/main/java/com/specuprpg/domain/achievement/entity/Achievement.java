package com.specuprpg.domain.achievement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// achievement 테이블 = 칭호 템플릿
// 어떤 조건을 달성하면 어떤 칭호를 주는지 정의돼 있어요
@Entity
@Table(name = "achievement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예: "7일 연속 달성자"
    @Column(nullable = false, length = 100)
    private String title;

    // 예: "7일 연속으로 퀘스트를 완료했어요"
    @Column(nullable = false, length = 255)
    private String description;

    // 칭호 부여 조건 종류
    // STREAK_DAYS / QUEST_COUNT / LEVEL / JOB_TIER / LOGIN_TIME
    @Column(nullable = false, length = 50)
    private String conditionType;

    // 조건 수치
    // 예: STREAK_DAYS + 7 → 7일 연속 달성
    @Column(nullable = false)
    private int conditionValue;

    @Column(length = 255)
    private String badgeImage;

    // 숨겨진 업적 여부
    // true면 달성 전엔 존재 자체가 비공개
    // 예: "새벽 4시 개발자" 같은 히든 칭호
    @Column(nullable = false)
    private boolean isHidden;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
