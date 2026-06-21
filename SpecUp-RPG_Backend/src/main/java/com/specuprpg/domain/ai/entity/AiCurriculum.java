package com.specuprpg.domain.ai.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ai_curriculum 테이블 = AI가 생성한 커리큘럼 저장소
// 진단 결과를 바탕으로 AI가 만든 주차별 학습 계획이 저장돼요
@Entity
@Table(name = "ai_curriculum")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiCurriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 진단 결과로 만들어진 커리큘럼인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private AiDiagnosis diagnosis;

    // 단기 목표 추가나 병합 시 원래 커리큘럼을 참조해요
    // 예: 정처기 단기 커리큘럼의 parentId = 원래 풀스택 커리큘럼 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AiCurriculum parent;

    // 예: "Spring Boot 입문 4주 커리큘럼"
    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private int totalWeeks;

    // AI가 생성한 주차별 퀘스트 목록을 JSON으로 저장
    // 예: [{"week": 1, "title": "기초", "quests": ["환경설정", "Hello World"]}]
    @Column(columnDefinition = "TEXT", nullable = false)
    private String roadmapJson;

    // 우선순위 (낮을수록 먼저)
    // 단기 목표 추가 시 기존 커리큘럼은 2, 새 커리큘럼은 1
    @Column(nullable = false)
    private int priority;

    // 일시정지 / 자동 복귀 날짜
    // 예: 정처기 시험 끝나면 자동으로 원래 커리큘럼 복귀
    private LocalDate pausedAt;
    private LocalDate resumeAt;

    // 병합된 커리큘럼 ID 목록 JSON
    // 예: "[2, 5]" → 2번, 5번 커리큘럼을 흡수해서 병합됨
    @Column(columnDefinition = "TEXT")
    private String mergedFrom;

    // 재조정된 시점 이력
    private LocalDateTime revisedAt;

    // 현재 활성화된 커리큘럼인지
    // false = 완료됐거나 비활성화된 커리큘럼
    @Column(nullable = false)
    private boolean isActive;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static AiCurriculum create(User user, AiDiagnosis diagnosis,
                                      String title, int totalWeeks, String roadmapJson) {
        AiCurriculum curriculum = new AiCurriculum();
        curriculum.user = user;
        curriculum.diagnosis = diagnosis;
        curriculum.title = title;
        curriculum.totalWeeks = totalWeeks;
        curriculum.roadmapJson = roadmapJson;
        curriculum.priority = 1;
        curriculum.isActive = true;
        return curriculum;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // 커리큘럼 일시정지 (단기 목표 추가 시)
    public void pause(LocalDate resumeAt) {
        this.pausedAt = LocalDate.now();
        this.resumeAt = resumeAt;
        this.priority = 2; // 우선순위 낮춤
    }

    // 커리큘럼 재개
    public void resume() {
        this.pausedAt = null;
        this.resumeAt = null;
        this.priority = 1;
    }

    // 커리큘럼 비활성화 (목표 완전 전환 시)
    public void deactivate() {
        this.isActive = false;
    }

    // 커리큘럼 재조정 (병합, 페이스 조정 시)
    public void revise(String newRoadmapJson) {
        this.roadmapJson = newRoadmapJson;
        this.revisedAt = LocalDateTime.now();
    }

    // 단기 목표 커리큘럼 연결
    public void setParent(AiCurriculum parent) {
        this.parent = parent;
    }
}
