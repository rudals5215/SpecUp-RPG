package com.specuprpg.domain.quest.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

// quest_completion 테이블 = 퀘스트 완료 이력
// 퀘스트를 완료할 때마다 여기에 기록이 쌓여요
//
// 중복 완료 방지 이중 방어 구조
// 1차 방어: DB 레벨 UNIQUE 제약 (user_quest_id + completed_date 조합이 중복되면 DB가 막음)
// 2차 방어: Service 레벨에서 이미 완료된 퀘스트인지 코드로 체크
@Entity
@Table(
    name = "quest_completion",
    uniqueConstraints = {
        // 같은 퀘스트를 하루에 두 번 완료할 수 없어요
        // DB가 자동으로 막아줘요
        @UniqueConstraint(columnNames = {"user_quest_id", "completed_date"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class QuestCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_quest_id", nullable = false)
    private UserQuest userQuest;

    // 완료한 날짜 (통계, 스트릭 계산에 사용)
    @Column(nullable = false)
    private LocalDate completedDate;

    // 실제로 지급된 XP와 골드를 스냅샷으로 저장해요
    // 나중에 퀘스트 보상값이 바뀌어도 과거 이력은 그대로 유지돼요
    // 영수증에 그날 가격이 찍히는 것처럼요
    @Column(nullable = false)
    private int xpGained;

    @Column(nullable = false)
    private int goldGained;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static QuestCompletion create(User user, UserQuest userQuest,
                                         int xpGained, int goldGained) {
        QuestCompletion completion = new QuestCompletion();
        completion.user = user;
        completion.userQuest = userQuest;
        completion.completedDate = LocalDate.now();
        completion.xpGained = xpGained;
        completion.goldGained = goldGained;
        return completion;
    }
}
