package com.specuprpg.domain.achievement.repository;

import com.specuprpg.domain.achievement.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    // 유저의 모든 칭호 조회
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findAllByUserId(@Param("userId") Long userId);

    // 특정 칭호 보유 여부 확인 (중복 부여 방지)
    boolean existsByUserIdAndAchievementId(Long userId, Long achievementId);
}
