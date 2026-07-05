package com.specuprpg.domain.user.service;

import com.specuprpg.domain.pet.repository.PetRepository;
import com.specuprpg.domain.quest.entity.UserQuest;
import com.specuprpg.domain.quest.repository.QuestCompletionRepository;
import com.specuprpg.domain.quest.repository.UserQuestRepository;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserQuestRepository userQuestRepository;           // 추가
//    private final QuestCompletionRepository questCompletionRepository; // 추가
    private final PetRepository petRepository;


    @Transactional(readOnly = true)
    public DashboardResponseDto.Dashboard getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 실제 오늘의 퀘스트 데이터 조회
        LocalDate today = LocalDate.now();
        List<UserQuest> todayQuests = userQuestRepository.findTodayQuests(userId, today);
        int totalCount = todayQuests.size();
        int completedCount = (int) todayQuests.stream()
                .filter(q -> "COMPLETED".equals(q.getStatus()))
                .count();
        int achievementRate = totalCount == 0 ? 0 : (completedCount * 100 / totalCount);

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
                .todayQuests(DashboardResponseDto.Dashboard.QuestSummary.builder()
                        .totalCount(totalCount)
                        .completedCount(completedCount)
                        .achievementRate(achievementRate)
                        .quests(todayQuests.stream()
                                .map(q -> DashboardResponseDto.Dashboard.QuestSummary.QuestItem.builder()
                                        .userQuestId(q.getId())
                                        .title(q.getQuest().getTitle())
                                        .status(q.getStatus())
                                        .rewardXp(q.getQuest().getRewardXp())
                                        .rewardGold(q.getQuest().getRewardGold())
                                        .build())
                                .toList())
                        .build())
                .pet(petRepository.findByUserId(userId)
                        .map(pet -> DashboardResponseDto.Dashboard.PetInfo.builder()
                                .name(pet.getName())
                                .status(pet.getStatus())
                                .hunger(pet.getHunger())
                                .level(pet.getLevel())
                                .build())
                        .orElse(null)) // 아래에서 해결
                .activeAiToken(user.getAiToken())
                .aiTokenMax(user.getAiTokenMax())
                .build();
    }
}