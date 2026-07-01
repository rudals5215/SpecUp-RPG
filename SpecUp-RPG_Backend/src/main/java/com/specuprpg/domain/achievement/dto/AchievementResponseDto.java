package com.specuprpg.domain.achievement.dto;

import com.specuprpg.domain.achievement.entity.UserAchievement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class AchievementResponseDto {

    // 내 칭호 목록 응답
    @Getter
    @Builder
    public static class MyAchievements {
        private int totalCount;
        private List<AchievementInfo> achievements;
    }

    // 칭호 하나의 정보
    @Getter
    @Builder
    public static class AchievementInfo {
        private Long achievementId;
        private String title;
        private String description;
        private String badgeImage;
        private LocalDateTime earnedAt;

        public static AchievementInfo from(UserAchievement ua) {
            return AchievementInfo.builder()
                    .achievementId(ua.getAchievement().getId())
                    .title(ua.getAchievement().getTitle())
                    .description(ua.getAchievement().getDescription())
                    .badgeImage(ua.getAchievement().getBadgeImage())
                    .earnedAt(ua.getEarnedAt())
                    .build();
        }
    }
}
