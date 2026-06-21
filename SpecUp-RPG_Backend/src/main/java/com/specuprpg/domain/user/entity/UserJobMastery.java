package com.specuprpg.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_job_mastery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserJobMastery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // BACKEND_DEV / FRONTEND_DEV / DESIGNER / EXAM_PREP / FITNESS / LANGUAGE
    @Column(nullable = false, length = 50)
    private String jobType;

    // AI 진단으로 결정: 1(입문) ~ 5(전문가)
    @Column(nullable = false)
    private int tier;

    @Column(nullable = false)
    private int jobXp;

    // AI가 진단한 현재 위치 요약
    @Column(length = 255)
    private String aiSummary;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static UserJobMastery create(User user, String jobType, int tier, String aiSummary) {
        UserJobMastery mastery = new UserJobMastery();
        mastery.user = user;
        mastery.jobType = jobType;
        mastery.tier = tier;
        mastery.jobXp = 0;
        mastery.aiSummary = aiSummary;
        return mastery;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────
    public void addJobXp(int xp) {
        this.jobXp += xp;
    }

    public void updateTier(int tier) {
        this.tier = tier;
    }

    public void updateAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }
}
