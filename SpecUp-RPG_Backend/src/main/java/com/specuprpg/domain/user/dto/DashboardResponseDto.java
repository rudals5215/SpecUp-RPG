package com.specuprpg.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DashboardResponseDto {

    @Getter
    @Builder
    public static class Dashboard {
        private UserInfo user;
        private QuestSummary todayQuests;
        private PetInfo pet;
        private int activeAiToken;
        private int aiTokenMax;

        @Getter
        @Builder
        public static class UserInfo {
            private String nickname;
            private int level;
            private int experiencePoints;
            private int nextLevelXp;
            private int progressPercent;
            private int gold;
            private int streakDays;
        }

        @Getter
        @Builder
        public static class QuestSummary {
            private int totalCount;
            private int completedCount;
            private int achievementRate;
            // 퀘스트 목록 추가
            private List<QuestItem> quests;

            @Getter
            @Builder
            public static class QuestItem {
                private Long userQuestId;
                private String title;
                private String status;
                private int rewardXp;
                private int rewardGold;
            }
        }

        @Getter
        @Builder
        public static class PetInfo {
            private String name;
            private String status;
            private int hunger;
            private int level;
        }
    }
}
