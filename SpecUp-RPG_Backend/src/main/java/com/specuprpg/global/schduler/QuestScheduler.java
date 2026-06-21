package com.specuprpg.global.scheduler;

import com.specuprpg.domain.quest.entity.Quest;
import com.specuprpg.domain.quest.entity.UserQuest;
import com.specuprpg.domain.quest.repository.QuestRepository;
import com.specuprpg.domain.quest.repository.UserQuestRepository;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// @Component = Spring이 이 클래스를 자동으로 관리해줘요
// @Slf4j = log.info() 같은 로그를 쉽게 쓸 수 있게 해줘요
@Slf4j
@Component
@RequiredArgsConstructor
public class QuestScheduler {

    private final UserRepository userRepository;
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;

    // ── 매일 자정: 퀘스트 초기화 & 일일 퀘스트 할당 ──────
    // cron = "0 0 0 * * *" → 매일 00시 00분 00초에 실행
    // 초 분 시 일 월 요일 순서예요
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetAndAssignDailyQuests() {
        log.info("[Scheduler] 일일 퀘스트 초기화 시작");

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // ① 어제 못 깬 퀘스트 만료 처리
        // 어제 날짜의 ASSIGNED 상태 퀘스트를 전부 EXPIRED로 바꿔요
        List<UserQuest> expiredQuests = userQuestRepository.findExpiredQuests(today);
        expiredQuests.forEach(UserQuest::expire);
        log.info("[Scheduler] 만료 처리 완료: {}개", expiredQuests.size());

        // ② 전체 유저에게 오늘 일일 퀘스트 할당
        // 나중에 AI 커리큘럼 연동 후 유저별 맞춤 퀘스트로 교체 예정
        List<User> allUsers = userRepository.findAll();

        // isTemplate=true 인 DAILY 퀘스트 목록 가져오기
        // 지금은 DEVELOPER 카테고리만. 나중에 유저별 카테고리에 맞게 변경 예정
        List<Quest> dailyTemplates = questRepository
                .findByCategoryAndQuestTypeAndIsTemplate("DEVELOPER", "DAILY", true);

        if (dailyTemplates.isEmpty()) {
            log.warn("[Scheduler] 할당할 일일 퀘스트 템플릿이 없어요!");
            return;
        }

        int assignedCount = 0;
        for (User user : allUsers) {
            // 오늘 이미 퀘스트가 할당됐는지 확인 (중복 할당 방지)
            List<UserQuest> todayQuests = userQuestRepository.findTodayQuests(user.getId(), today);
            if (!todayQuests.isEmpty()) {
                continue; // 이미 오늘 퀘스트가 있으면 건너뜀
            }

            // 템플릿 퀘스트를 유저한테 할당
            for (Quest quest : dailyTemplates) {
                UserQuest userQuest = UserQuest.assign(user, quest, today);
                userQuestRepository.save(userQuest);
                assignedCount++;
            }
        }

        log.info("[Scheduler] 일일 퀘스트 할당 완료: 유저 {}명, 퀘스트 {}개",
                allUsers.size(), assignedCount);
    }

    // ── 매일 밤 22시: AI 일일 피드백 생성 ────────────────
    // AI 도메인 개발 후 활성화 예정
    // 지금은 로그만 찍어요
    @Scheduled(cron = "0 0 22 * * *")
    public void generateDailyFeedback() {
        log.info("[Scheduler] AI 일일 피드백 생성 시작 (AI 도메인 연동 후 활성화 예정)");
        // TODO: AI 도메인 연동 후 구현
    }

    // ── 1분마다: AI 토큰 자동 충전 ───────────────────────
    // reset_at 시각이 지난 유저의 AI 토큰을 자동으로 충전해요
    // Claude.ai처럼 시간이 지나면 토큰이 자동으로 채워지는 구조예요
    @Scheduled(fixedRate = 60000) // 60000ms = 1분마다 실행
    @Transactional
    public void refillAiTokens() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime nextResetAt = now.plusHours(24);

        int updatedCount = userRepository.refillExpiredTokens(now, nextResetAt);

        // 충전된 유저가 있을 때만 로그 출력 (매분 로그 도배 방지)
        if (updatedCount > 0) {
            log.info("[Scheduler] AI 토큰 충전 완료: {}명", updatedCount);
        }
    }
}
