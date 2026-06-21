package com.specuprpg.domain.user.service;

import com.specuprpg.domain.user.dto.StatsResponseDto;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserStatusRepository userStatusRepository;

    // ── 주간 달성 통계 ────────────────────────────────────
    // 퀘스트 완료 이력은 퀘스트 도메인 개발 후 연동 예정
    // 현재는 기본 구조만 반환
    @Transactional(readOnly = true)
    public StatsResponseDto.WeeklyStats getWeeklyStats(Long userId) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이번 주 월요일 ~ 일요일 계산
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        // 퀘스트 완료 이력 연동 전 빈 데이터 반환
        List<StatsResponseDto.WeeklyStats.DailyStat> dailyStats = new ArrayList<>();
        for (LocalDate date = weekStart; !date.isAfter(today); date = date.plusDays(1)) {
            dailyStats.add(StatsResponseDto.WeeklyStats.DailyStat.builder()
                    .date(date)
                    .completedCount(0)
                    .totalCount(0)
                    .rate(0)
                    .build());
        }

        return StatsResponseDto.WeeklyStats.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .dailyStats(dailyStats)
                .totalXpGained(0)    // 퀘스트 도메인 연동 후 실제 데이터로 교체
                .totalGoldGained(0)
                .bestStreak(status.getStreakDays())
                .build();
    }
}
