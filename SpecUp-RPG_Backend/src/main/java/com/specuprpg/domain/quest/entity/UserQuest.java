package com.specuprpg.domain.quest.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

// user_quest 테이블 = 내가 주문한 퀘스트 목록
// quest(메뉴판)에서 유저가 선택하거나 시스템이 자동 할당한 퀘스트가 여기 기록돼요
@Entity
@Table(name = "user_quest")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저한테 할당된 퀘스트인지
    // FetchType.LAZY = 필요할 때만 DB에서 유저 정보를 가져와요 (성능 최적화)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 퀘스트인지 (메뉴판에서 어떤 메뉴를 골랐는지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    // 퀘스트 현재 상태
    // ASSIGNED = 진행 중 (아직 완료 안 함)
    // COMPLETED = 완료
    // EXPIRED = 기간 만료 (일일 퀘스트가 오늘 안에 못 깼을 때)
    @Column(nullable = false, length = 20)
    private String status;

    // 이 퀘스트의 마감일
    // 일일 퀘스트면 오늘 날짜, 주간 퀘스트면 이번 주 일요일
    private LocalDate dueDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static UserQuest assign(User user, Quest quest, LocalDate dueDate) {
        UserQuest userQuest = new UserQuest();
        userQuest.user = user;
        userQuest.quest = quest;
        userQuest.status = "ASSIGNED"; // 처음엔 항상 진행 중
        userQuest.dueDate = dueDate;
        return userQuest;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // 퀘스트 완료 처리
    public void complete() {
        this.status = "COMPLETED";
    }

    // 퀘스트 만료 처리 (Scheduler가 매일 자정에 호출)
    public void expire() {
        this.status = "EXPIRED";
    }

    // 완료된 퀘스트인지 확인
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }

    // 내 퀘스트인지 확인 (다른 유저 퀘스트를 완료 못 하게 막는 용도)
    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }
}
