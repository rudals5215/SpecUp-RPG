package com.specuprpg.domain.user.dto;

import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.entity.UserJobMastery;
import com.specuprpg.domain.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {

    // ── 내 정보 조회 ──────────────────────────────────────
    @Getter
    @Builder
    public static class MyInfo {
        private Long id;
        private String email;
        private String nickname;
        private String role;
        private int aiToken;
        private int aiTokenMax;
        private LocalDateTime aiTokenResetAt;
        private StatusInfo status;
        private List<JobMasteryInfo> jobMasteries;

        @Getter
        @Builder
        public static class StatusInfo {
            private int level;
            private int experiencePoints;
            private int nextLevelXp;
            private int progressPercent;
            private int gold;
            private int streakDays;
        }

        @Getter
        @Builder
        public static class JobMasteryInfo {
            private String jobType;
            private int tier;
            private int jobXp;
            private String aiSummary;
        }

        public static MyInfo of(User user, UserStatus status, List<UserJobMastery> masteries) {
            return MyInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .aiToken(user.getAiToken())
                    .aiTokenMax(user.getAiTokenMax())
                    .aiTokenResetAt(user.getAiTokenResetAt())
                    .status(StatusInfo.builder()
                            .level(status.getLevel())
                            .experiencePoints(status.getExperiencePoints())
                            .nextLevelXp(status.getNextLevelXp())
                            .progressPercent(status.getProgressPercent())
                            .gold(status.getGold())
                            .streakDays(status.getStreakDays())
                            .build())
                    .jobMasteries(masteries.stream()
                            .map(m -> JobMasteryInfo.builder()
                                    .jobType(m.getJobType())
                                    .tier(m.getTier())
                                    .jobXp(m.getJobXp())
                                    .aiSummary(m.getAiSummary())
                                    .build())
                            .toList())
                    .build();
        }
    }

    // ── 닉네임 수정 ───────────────────────────────────────
    @Getter
    @Builder
    public static class UpdateNickname {
        private String nickname;

        public static UpdateNickname of(String nickname) {
            return UpdateNickname.builder()
                    .nickname(nickname)
                    .build();
        }
    }

    // ── AI 토큰 현황 ──────────────────────────────────────
    @Getter
    @Builder
    public static class AiTokenInfo {
        private int aiToken;
        private int aiTokenMax;
        private LocalDateTime aiTokenResetAt;
        private long resetInHours;

        public static AiTokenInfo of(User user, long resetInHours) {
            return AiTokenInfo.builder()
                    .aiToken(user.getAiToken())
                    .aiTokenMax(user.getAiTokenMax())
                    .aiTokenResetAt(user.getAiTokenResetAt())
                    .resetInHours(resetInHours)
                    .build();
        }
    }
}
