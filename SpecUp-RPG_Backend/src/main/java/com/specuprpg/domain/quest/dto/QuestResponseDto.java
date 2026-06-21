package com.specuprpg.domain.quest.dto;

import com.specuprpg.domain.quest.entity.UserQuest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class QuestResponseDto {

    // 퀘스트 하나의 정보
    @Getter
    @Builder
    public static class QuestInfo {
        private Long userQuestId;   // user_quest 테이블의 ID (완료 처리할 때 필요)
        private Long questId;       // quest 테이블의 ID
        private String title;
        private String description;
        private String questType;
        private String category;
        private int rewardXp;
        private int rewardGold;
        private String status;      // ASSIGNED / COMPLETED / EXPIRED
        private LocalDate dueDate;

        // Entity를 DTO로 변환하는 메서드
        // Entity를 그대로 프론트에 주면 안 되니까 필요한 것만 골라서 담아요
        public static QuestInfo from(UserQuest userQuest) {
            return QuestInfo.builder()
                    .userQuestId(userQuest.getId())
                    .questId(userQuest.getQuest().getId())
                    .title(userQuest.getQuest().getTitle())
                    .description(userQuest.getQuest().getDescription())
                    .questType(userQuest.getQuest().getQuestType())
                    .category(userQuest.getQuest().getCategory())
                    .rewardXp(userQuest.getQuest().getRewardXp())
                    .rewardGold(userQuest.getQuest().getRewardGold())
                    .status(userQuest.getStatus())
                    .dueDate(userQuest.getDueDate())
                    .build();
        }
    }

    // 오늘의 퀘스트 목록 응답
    @Getter
    @Builder
    public static class TodayQuests {
        private LocalDate date;
        private int totalCount;       // 오늘 전체 퀘스트 수
        private int completedCount;   // 완료한 퀘스트 수
        private int achievementRate;  // 달성률 (%)
        private List<QuestInfo> quests;
    }

    // 퀘스트 완료 응답 — 가장 중요한 응답이에요!
    // 완료하면 XP/골드가 얼마나 올랐는지, 레벨업 했는지, 새 칭호가 생겼는지 다 알려줘요
    @Getter
    @Builder
    public static class CompleteResult {
        private String questTitle;
        private int xpGained;
        private int goldGained;
        private UserStatusResult userStatus;
        private List<String> newAchievements; // 새로 획득한 칭호 목록

        @Getter
        @Builder
        public static class UserStatusResult {
            private int level;
            private int experiencePoints;
            private int gold;
            private int streakDays;
            private boolean isLevelUp;       // 레벨업 했는지
            private Integer previousLevel;   // 레벨업 전 레벨 (레벨업 했을 때만 표시)
        }
    }

    // 커스텀 퀘스트 생성 응답
    @Getter
    @Builder
    public static class CreateResult {
        private Long userQuestId;
        private String title;
        private String status;
    }
}
