package com.specuprpg.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User와 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int experiencePoints;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private int streakDays;

    private LocalDate lastActiveDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    // 회원가입 시 자동으로 Lv.1로 생성
    public static UserStatus createDefault(User user) {
        UserStatus status = new UserStatus();
        status.user = user;
        status.level = 1;
        status.experiencePoints = 0;
        status.gold = 0;
        status.streakDays = 0;
        status.lastActiveDate = null;
        return status;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // XP 추가 → 레벨업 여부 반환
    public boolean addExperience(int xp) {
        this.experiencePoints += xp;
        return checkLevelUp();
    }

    // 골드 추가
    public void addGold(int gold) {
        this.gold += gold;
    }

    // 골드 차감 (아이템 구매 등)
    // 골드 부족하면 false 반환
    public boolean useGold(int amount) {
        if (this.gold < amount) {
            return false;
        }
        this.gold -= amount;
        return true;
    }

    // 레벨업 체크
    // 레벨업 공식: 다음 레벨 필요 XP = 현재 레벨 * 200
    // 예: Lv.1 → Lv.2: 200 XP / Lv.5 → Lv.6: 1000 XP
    private boolean checkLevelUp() {
        int requiredXp = this.level * 200;
        if (this.experiencePoints >= requiredXp) {
            this.experiencePoints -= requiredXp;
            this.level++;
            return true;    // 레벨업 발생
        }
        return false;       // 레벨업 없음
    }

    // 다음 레벨까지 필요한 XP
    public int getNextLevelXp() {
        return this.level * 200;
    }

    // 현재 레벨 진행률 (%)
    public int getProgressPercent() {
        int requiredXp = this.level * 200;
        return (int) ((double) this.experiencePoints / requiredXp * 100);
    }

    // 스트릭 업데이트
    // 오늘 처음 활동이면 streakDays + 1
    // 어제 활동 안 했으면 streakDays 리셋
    public void updateStreak() {
        LocalDate today = LocalDate.now();
        if (lastActiveDate == null) {
            // 첫 활동
            this.streakDays = 1;
        } else if (lastActiveDate.isEqual(today.minusDays(1))) {
            // 어제 활동 → 연속 달성
            this.streakDays++;
        } else if (!lastActiveDate.isEqual(today)) {
            // 어제도 아니고 오늘도 아니면 → 스트릭 리셋
            this.streakDays = 1;
        }
        // 오늘 이미 활동했으면 streakDays 그대로
        this.lastActiveDate = today;
    }
}
