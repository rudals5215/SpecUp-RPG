package com.specuprpg.domain.achievement.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// user_achievement 테이블 = 유저가 획득한 칭호 목록
// 조건 달성 시 자동으로 INSERT돼요
@Entity
@Table(
    name = "user_achievement",
    uniqueConstraints = {
        // 같은 칭호를 두 번 획득할 수 없어요
        @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime earnedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static UserAchievement create(User user, Achievement achievement) {
        UserAchievement ua = new UserAchievement();
        ua.user = user;
        ua.achievement = achievement;
        return ua;
    }
}
