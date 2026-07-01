package com.specuprpg.domain.alarm.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// alarm_setting 테이블 = 유저별 알람 설정
// users와 1:1 관계 → 회원가입 시 기본값으로 자동 생성
@Entity
@Table(name = "alarm_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AlarmSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 아침 퀘스트 알람 (기본 08:00)
    @Column(nullable = false)
    private boolean morningAlarm;

    @Column(nullable = false, length = 5)
    private String morningTime;

    // 오후 중간 체크 알람 (미완료 유저에게만)
    @Column(nullable = false)
    private boolean afternoonAlarm;

    // 마감 전 알람 (기본 21:00)
    @Column(nullable = false)
    private boolean deadlineAlarm;

    // 3일 이상 미접속 시 복귀 알람
    @Column(nullable = false)
    private boolean comebackAlarm;

    // 레벨업 직전 특별 알람
    @Column(nullable = false)
    private boolean levelupAlarm;

    // 모바일 푸시 알람 전체 ON/OFF
    @Column(nullable = false)
    private boolean pushEnabled;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    // 회원가입 시 기본값으로 자동 생성
    public static AlarmSetting createDefault(User user) {
        AlarmSetting alarm = new AlarmSetting();
        alarm.user = user;
        alarm.morningAlarm = true;
        alarm.morningTime = "08:00";
        alarm.afternoonAlarm = true;
        alarm.deadlineAlarm = true;
        alarm.comebackAlarm = true;
        alarm.levelupAlarm = true;
        alarm.pushEnabled = true;
        return alarm;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────
    public void update(boolean morningAlarm, String morningTime,
                       boolean afternoonAlarm, boolean deadlineAlarm,
                       boolean comebackAlarm, boolean levelupAlarm,
                       boolean pushEnabled) {
        this.morningAlarm = morningAlarm;
        this.morningTime = morningTime;
        this.afternoonAlarm = afternoonAlarm;
        this.deadlineAlarm = deadlineAlarm;
        this.comebackAlarm = comebackAlarm;
        this.levelupAlarm = levelupAlarm;
        this.pushEnabled = pushEnabled;
    }
}
