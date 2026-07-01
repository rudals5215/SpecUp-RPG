package com.specuprpg.domain.pet.entity;

import com.specuprpg.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// pet 테이블 = 유저의 펫 정보
// users와 1:1 관계 → 회원가입 시 자동으로 생성돼요
// 퀘스트를 깰수록 펫이 성장하고, 방치하면 배고파져요
@Entity
@Table(name = "pet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // users와 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 펫 이름 (유저가 변경 가능)
    @Column(nullable = false, length = 50)
    private String name;

    // 직업별 전용 펫 타입
    // DEFAULT / SERVER_SPIRIT(백엔드) / PIXEL_CAT(프론트) / BRUSH_SPIRIT(디자이너) / OWL(수험생)
    @Column(nullable = false, length = 50)
    private String petType;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int exp;

    // 배고픔 수치: 0~100
    // 퀘스트 완료 시 증가, 시간 경과 시 감소
    // 0이 되면 컨디션 저하 → 복귀 유도 알람
    @Column(nullable = false)
    private int hunger;

    // 펫 상태
    // EGG(알) → BABY(아기) → ADULT(성체) → EVOLVED(진화)
    @Column(nullable = false, length = 20)
    private String status;

    // 진화한 시각 (null이면 아직 진화 안 함)
    private LocalDateTime evolvedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── 생성 메서드 ───────────────────────────────────────
    // 회원가입 시 자동으로 알 상태로 생성
    public static Pet createDefault(User user) {
        Pet pet = new Pet();
        pet.user = user;
        pet.name = "뭉이";         // 기본 이름
        pet.petType = "DEFAULT";
        pet.level = 1;
        pet.exp = 0;
        pet.hunger = 100;           // 처음엔 배부른 상태
        pet.status = "EGG";         // 알 상태로 시작
        return pet;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────

    // 펫 이름 변경
    public void updateName(String name) {
        this.name = name;
    }

    // 퀘스트 완료 시 펫 경험치 증가
    // 경험치가 일정량 쌓이면 레벨업
    public void gainExp(int amount) {
        this.exp += amount;
        this.hunger = Math.min(100, this.hunger + 10); // 배고픔도 회복

        // 레벨업 체크 (레벨당 100 exp 필요)
        if (this.exp >= 100) {
            this.exp -= 100;
            this.level++;
        }
    }

    // 배고픔 감소 (Scheduler가 주기적으로 호출)
    // 배고픔이 0이 되면 컨디션 저하 상태
    public void decreaseHunger(int amount) {
        this.hunger = Math.max(0, this.hunger - amount);
    }

    // 펫 진화
    // EGG → BABY → ADULT → EVOLVED 순서로 진화
    public boolean tryEvolve() {
        String nextStatus = switch (this.status) {
            case "EGG" -> "BABY";
            case "BABY" -> "ADULT";
            case "ADULT" -> "EVOLVED";
            default -> null; // EVOLVED는 더 이상 진화 불가
        };

        if (nextStatus != null) {
            this.status = nextStatus;
            this.evolvedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // 펫이 배고픈지 확인 (hunger가 30 이하면 배고픔)
    public boolean isHungry() {
        return this.hunger <= 30;
    }

    // 펫 컨디션 상태 반환
    // GOOD(양호) / HUNGRY(배고픔) / STARVING(굶주림)
    public String getCondition() {
        if (this.hunger > 60) return "GOOD";
        if (this.hunger > 30) return "HUNGRY";
        return "STARVING";
    }

    // 진화 가능 여부 확인
    public boolean canEvolve() {
        return !this.status.equals("EVOLVED");
    }

    // 진화 조건 메시지 반환
    public String getEvolveCondition() {
        return switch (this.status) {
            case "EGG" -> "7일 연속 퀘스트 달성 시 부화해요!";
            case "BABY" -> "레벨 10 달성 시 성체로 진화해요!";
            case "ADULT" -> "레벨 20 달성 시 최종 진화해요!";
            default -> "최종 진화 완료!";
        };
    }
}
