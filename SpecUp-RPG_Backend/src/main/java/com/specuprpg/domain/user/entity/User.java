package com.specuprpg.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    // DEVELOPER / DESIGNER / EXAM_PREP / FITNESS / LANGUAGE
    @Column(nullable = false, length = 30)
    private String role;

    // AI 토큰 시스템
    @Column(nullable = false)
    private int aiToken;

    @Column(nullable = false)
    private int aiTokenMax;

    @Column(nullable = false)
    private LocalDateTime aiTokenResetAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    public static User create(String email, String password, String nickname) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.role = "DEVELOPER";                              // 기본값
        user.aiToken = 10;
        user.aiTokenMax = 10;
        user.aiTokenResetAt = LocalDateTime.now().plusHours(24);
        return user;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // 닉네임 변경
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    // AI 토큰 차감
    // 토큰 부족하면 false 반환 → 서비스에서 예외 처리
    public boolean useAiToken(int amount) {
        if (this.aiToken < amount) {
            return false;
        }
        this.aiToken -= amount;
        return true;
    }

    // AI 토큰 충전 (Scheduler가 주기적으로 호출)
    public void refillAiToken() {
        this.aiToken = this.aiTokenMax;
        this.aiTokenResetAt = LocalDateTime.now().plusHours(24);
    }

    // role 변경 (나중에 카테고리 확장 시 사용)
    public void updateRole(String role) {
        this.role = role;
    }
}
