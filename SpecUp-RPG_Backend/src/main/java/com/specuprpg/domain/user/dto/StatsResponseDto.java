package com.specuprpg.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class StatsResponseDto {

    @Getter
    @Builder
    public static class WeeklyStats {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private List<DailyStat> dailyStats;
        private int totalXpGained;
        private int totalGoldGained;
        private int bestStreak;

        @Getter
        @Builder
        public static class DailyStat {
            private LocalDate date;
            private int completedCount;
            private int totalCount;
            private int rate;
        }
    }
}
