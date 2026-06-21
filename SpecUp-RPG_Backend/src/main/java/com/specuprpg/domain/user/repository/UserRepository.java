package com.specuprpg.domain.user.repository;

import com.specuprpg.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 유저 조회 (로그인, 중복 체크)
    Optional<User> findByEmail(String email);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // AI 토큰 자동 충전 (Scheduler용)
    // reset_at 시각이 지난 유저 전체 토큰 충전
    @Modifying
    @Query("""
        UPDATE User u
        SET u.aiToken = u.aiTokenMax,
            u.aiTokenResetAt = :nextResetAt
        WHERE u.aiTokenResetAt <= :now
    """)
    int refillExpiredTokens(LocalDateTime now, LocalDateTime nextResetAt);
}
