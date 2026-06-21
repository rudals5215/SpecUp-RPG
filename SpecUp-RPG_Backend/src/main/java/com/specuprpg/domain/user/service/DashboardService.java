package com.specuprpg.domain.user.service;

import com.specuprpg.domain.user.dto.DashboardResponseDto;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // ── 대시보드 통합 조회 ────────────────────────────────
    // 퀘스트, 펫 데이터는 해당 도메인 개발 후 연동 예정
    @Transactional(readOnly = true)
    public DashboardResponseDto.Dashboard getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return DashboardResponseDto.Dashboard.builder()
                .user(DashboardResponseDto.Dashboard.UserInfo.builder()
                        .nickname(user.getNickname())
                        .level(status.getLevel())
                        .experiencePoints(status.getExperiencePoints())
                        .nextLevelXp(status.getNextLevelXp())
                        .progressPercent(status.getProgressPercent())
                        .gold(status.getGold())
                        .streakDays(status.getStreakDays())
                        .build())
                // 퀘스트 시스템 개발 후 실제 데이터로 교체 예정
                .todayQuests(DashboardResponseDto.Dashboard.QuestSummary.builder()
                        .totalCount(0)
                        .completedCount(0)
                        .achievementRate(0)
                        .build())
                // 펫 시스템 개발 후 실제 데이터로 교체 예정
                .pet(null)
                .activeAiToken(user.getAiToken())
                .aiTokenMax(user.getAiTokenMax())
                .build();
    }
}
