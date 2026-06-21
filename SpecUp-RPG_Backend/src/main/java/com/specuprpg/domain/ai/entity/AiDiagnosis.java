package com.specuprpg.domain.ai.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ai_diagnosis 테이블 = AI 진단 결과 저장소
// 유저와 AI가 나눈 온보딩 대화 결과를 저장해요
// 나중에 재진단할 때 이 이력을 AI한테 컨텍스트로 넘겨줘요
@Entity
@Table(name = "ai_diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // AI가 판단한 실력 레벨
    // BEGINNER(초보) / INTERMEDIATE(중급) / ADVANCED(고급)
    @Column(nullable = false, length = 20)
    private String assessedLevel;

    // AI가 파악한 목표 요약
    // 예: "Spring Boot 백엔드 취업 준비"
    @Column(length = 255)
    private String goalSummary;

    // 하루 가용 시간 (시간 단위)
    // 예: 2.5 = 하루 2시간 30분
    @Column(precision = 3, scale = 1)
    private BigDecimal dailyHours;

    // AI와 주고받은 전체 대화 내용을 JSON으로 저장
    // 재진단 시 이걸 AI한테 넘겨서 맥락을 유지해요
    // columnDefinition = "TEXT" → JSON을 문자열로 저장
    @Column(columnDefinition = "TEXT")
    private String conversation;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static AiDiagnosis create(User user, String assessedLevel,
                                     String goalSummary, BigDecimal dailyHours,
                                     String conversation) {
        AiDiagnosis diagnosis = new AiDiagnosis();
        diagnosis.user = user;
        diagnosis.assessedLevel = assessedLevel;
        diagnosis.goalSummary = goalSummary;
        diagnosis.dailyHours = dailyHours;
        diagnosis.conversation = conversation;
        return diagnosis;
    }
}
