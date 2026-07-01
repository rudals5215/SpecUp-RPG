package com.specuprpg.domain.feedback.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// ai_feedback 테이블 = AI 피드백 이력
// Scheduler가 매일/주간/월간/연간 주기로 생성해서 저장해요
@Entity
@Table(name = "ai_feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 피드백 주기
    // DAILY / WEEKLY / MONTHLY / YEARLY
    @Column(nullable = false, length = 20)
    private String feedbackType;

    // AI가 생성한 피드백 내용
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 피드백 생성 시점의 유저 스탯 스냅샷 (JSON)
    // 나중에 "그때 내 레벨이 얼마였지?" 확인용
    @Column(columnDefinition = "TEXT")
    private String statsSnapshot;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static AiFeedback create(User user, String feedbackType,
                                    String content, String statsSnapshot) {
        AiFeedback feedback = new AiFeedback();
        feedback.user = user;
        feedback.feedbackType = feedbackType;
        feedback.content = content;
        feedback.statsSnapshot = statsSnapshot;
        return feedback;
    }
}
