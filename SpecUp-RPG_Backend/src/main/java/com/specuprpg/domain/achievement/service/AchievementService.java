package com.specuprpg.domain.achievement.service;

import com.specuprpg.domain.achievement.dto.AchievementResponseDto;
import com.specuprpg.domain.achievement.entity.Achievement;
import com.specuprpg.domain.achievement.entity.UserAchievement;
import com.specuprpg.domain.achievement.repository.AchievementRepository;
import com.specuprpg.domain.achievement.repository.UserAchievementRepository;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    // ── 내 칭호 목록 조회 ─────────────────────────────────
    @Transactional(readOnly = true)
    public AchievementResponseDto.MyAchievements getMyAchievements(Long userId) {
        List<UserAchievement> userAchievements =
                userAchievementRepository.findAllByUserId(userId);

        List<AchievementResponseDto.AchievementInfo> achievements = userAchievements
                .stream()
                .map(AchievementResponseDto.AchievementInfo::from)
                .toList();

        return AchievementResponseDto.MyAchievements.builder()
                .totalCount(achievements.size())
                .achievements(achievements)
                .build();
    }

    // ── 칭호 조건 체크 및 자동 부여 ──────────────────────
    // 퀘스트 완료, 레벨업, 스트릭 업데이트 시 호출돼요
    // 새로 획득한 칭호 목록을 반환해요 (퀘스트 완료 응답에 포함)
    @Transactional
    public List<String> checkAndGrantAchievements(Long userId, String conditionType, int value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 해당 조건에 맞는 칭호 목록 조회
        // 예: STREAK_DAYS + 7이면 1일, 3일, 7일 칭호 전부 체크
        List<Achievement> candidates = achievementRepository
                .findByConditionTypeAndConditionValueLessThanEqual(conditionType, value);

        List<String> newAchievements = new ArrayList<>();

        for (Achievement achievement : candidates) {
            // 이미 보유한 칭호면 건너뜀 (중복 방지)
            if (userAchievementRepository.existsByUserIdAndAchievementId(
                    userId, achievement.getId())) {
                continue;
            }

            // 새 칭호 부여
            UserAchievement userAchievement = UserAchievement.create(user, achievement);
            userAchievementRepository.save(userAchievement);
            newAchievements.add(achievement.getTitle());

            log.info("[칭호 획득] userId={}, title={}", userId, achievement.getTitle());
        }

        return newAchievements;
    }
}
