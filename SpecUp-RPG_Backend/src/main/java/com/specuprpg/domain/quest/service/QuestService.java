package com.specuprpg.domain.quest.service;

import com.specuprpg.domain.achievement.service.AchievementService;
import com.specuprpg.domain.quest.dto.QuestRequestDto;
import com.specuprpg.domain.quest.dto.QuestResponseDto;
import com.specuprpg.domain.quest.entity.Quest;
import com.specuprpg.domain.quest.entity.QuestCompletion;
import com.specuprpg.domain.quest.entity.UserQuest;
import com.specuprpg.domain.quest.repository.QuestCompletionRepository;
import com.specuprpg.domain.quest.repository.QuestRepository;
import com.specuprpg.domain.quest.repository.UserQuestRepository;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final QuestCompletionRepository questCompletionRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final AchievementService achievementService;

    // 수정 — 실제 칭호 체크
    List<String> newAchievements = new ArrayList<>();

    // ── 오늘의 퀘스트 목록 조회 ──────────────────────────
    @Transactional(readOnly = true)
    public QuestResponseDto.TodayQuests getTodayQuests(Long userId) {
        LocalDate today = LocalDate.now();
        List<UserQuest> userQuests = userQuestRepository.findTodayQuests(userId, today);

        // 완료한 퀘스트 수 계산
        int completedCount = (int) userQuests.stream()
                .filter(UserQuest::isCompleted)
                .count();

        // 달성률 계산 (완료 / 전체 * 100)
        int totalCount = userQuests.size();
        int achievementRate = totalCount == 0 ? 0 : (completedCount * 100 / totalCount);

        return QuestResponseDto.TodayQuests.builder()
                .date(today)
                .totalCount(totalCount)
                .completedCount(completedCount)
                .achievementRate(achievementRate)
                .quests(userQuests.stream()
                        .map(QuestResponseDto.QuestInfo::from)
                        .toList())
                .build();
    }

    // ── 주간 퀘스트 목록 조회 ─────────────────────────────
    @Transactional(readOnly = true)
    public List<QuestResponseDto.QuestInfo> getWeeklyQuests(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        return userQuestRepository.findWeeklyQuests(userId, weekStart, weekEnd)
                .stream()
                .map(QuestResponseDto.QuestInfo::from)
                .toList();
    }

    // ── 커스텀 퀘스트 생성 ────────────────────────────────
    // 유저가 직접 퀘스트를 만드는 기능이에요
    @Transactional
    public QuestResponseDto.CreateResult createQuest(Long userId, QuestRequestDto.Create request) {
        User user = getUserById(userId);

        // 1. 퀘스트 메뉴판에 새 퀘스트 추가
        Quest quest = Quest.createCustom(
                request.getTitle(),
                request.getDescription(),
                request.getQuestType(),
                request.getCategory(),
                request.getRewardXp(),
                request.getRewardGold()
        );
        questRepository.save(quest);

        // 2. 유저에게 해당 퀘스트 할당
        // 마감일이 없으면 오늘 날짜로 설정
        LocalDate dueDate = request.getDueDate() != null
                ? request.getDueDate()
                : LocalDate.now();

        UserQuest userQuest = UserQuest.assign(user, quest, dueDate);
        userQuestRepository.save(userQuest);

        log.info("[퀘스트 생성] userId={}, title={}", userId, quest.getTitle());

        return QuestResponseDto.CreateResult.builder()
                .userQuestId(userQuest.getId())
                .title(quest.getTitle())
                .status(userQuest.getStatus())
                .build();
    }

    // ── 퀘스트 완료 처리 ⭐ 핵심 기능 ─────────────────────
    @Transactional
    public QuestResponseDto.CompleteResult completeQuest(Long userId, Long userQuestId) {

        // ① 퀘스트 조회
        UserQuest userQuest = userQuestRepository.findById(userQuestId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));

        // ② 내 퀘스트인지 확인 (다른 사람 퀘스트 완료 못 하게 막아요)
        if (!userQuest.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.QUEST_NOT_MINE);
        }

        // ③ 이미 완료한 퀘스트인지 확인 (중복 방지 2차 방어)
        // 1차 방어는 DB UNIQUE 제약이 막아줘요
        if (userQuest.isCompleted()) {
            throw new CustomException(ErrorCode.QUEST_ALREADY_COMPLETED);
        }

        // 오늘 날짜로 중복 완료 한 번 더 체크
        boolean alreadyCompleted = questCompletionRepository
                .existsByUserQuestIdAndCompletedDate(userQuestId, LocalDate.now());
        if (alreadyCompleted) {
            throw new CustomException(ErrorCode.QUEST_ALREADY_COMPLETED);
        }

        // ④ 퀘스트 완료 처리
        userQuest.complete();

        // ⑤ 완료 이력 저장 (보상값 스냅샷 포함)
        User user = getUserById(userId);
        int xpGained = userQuest.getQuest().getRewardXp();
        int goldGained = userQuest.getQuest().getRewardGold();

        QuestCompletion completion = QuestCompletion.create(user, userQuest, xpGained, goldGained);
        questCompletionRepository.save(completion);

        // ⑥ 유저 스탯 업데이트 (XP + 골드 추가)
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int previousLevel = status.getLevel();
        boolean isLevelUp = status.addExperience(xpGained); // 레벨업 여부 반환
        status.addGold(goldGained);
        status.updateStreak(); // 스트릭 업데이트

        log.info("[퀘스트 완료] userId={}, questId={}, xp={}, gold={}, levelUp={}",
                userId, userQuestId, xpGained, goldGained, isLevelUp);

        // 퀘스트 완료 횟수 체크
        int completionCount = questCompletionRepository
                .countByUserIdAndCompletedDate(userId, LocalDate.now());
        newAchievements.addAll(achievementService
                .checkAndGrantAchievements(userId, "QUEST_COUNT", completionCount));

        // 스트릭 체크
        newAchievements.addAll(achievementService
                .checkAndGrantAchievements(userId, "STREAK_DAYS", status.getStreakDays()));

        // 레벨업 시 레벨 체크
        if (isLevelUp) {
            newAchievements.addAll(achievementService
                    .checkAndGrantAchievements(userId, "LEVEL", status.getLevel()));
        }

        // ⑦ 결과 반환
        // 새 칭호 획득 여부는 칭호 도메인 개발 후 연동 예정
        return QuestResponseDto.CompleteResult.builder()
                .questTitle(userQuest.getQuest().getTitle())
                .xpGained(xpGained)
                .goldGained(goldGained)
                .userStatus(QuestResponseDto.CompleteResult.UserStatusResult.builder()
                        .level(status.getLevel())
                        .experiencePoints(status.getExperiencePoints())
                        .gold(status.getGold())
                        .streakDays(status.getStreakDays())
                        .isLevelUp(isLevelUp)
                        .previousLevel(isLevelUp ? previousLevel : null)
                        .build())
                .newAchievements(new ArrayList<>()) // 칭호 도메인 연동 후 채울 예정
                .build();


    }

    // ── 퀘스트 삭제 (커스텀 퀘스트만 가능) ───────────────
    @Transactional
    public void deleteQuest(Long userId, Long userQuestId) {
        UserQuest userQuest = userQuestRepository.findById(userQuestId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUEST_NOT_FOUND));

        // 내 퀘스트인지 확인
        if (!userQuest.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.QUEST_NOT_MINE);
        }

        // 시스템 템플릿 퀘스트는 삭제 불가
        if (userQuest.getQuest().isTemplate()) {
            throw new CustomException(ErrorCode.TEMPLATE_QUEST_CANNOT_DELETE);
        }

        userQuestRepository.delete(userQuest);
        log.info("[퀘스트 삭제] userId={}, userQuestId={}", userId, userQuestId);
    }



    // ── 공통 유틸 ─────────────────────────────────────────
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
