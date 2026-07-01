package com.specuprpg.domain.feedback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.specuprpg.domain.feedback.dto.FeedbackResponseDto;
import com.specuprpg.domain.feedback.entity.AiFeedback;
import com.specuprpg.domain.feedback.repository.AiFeedbackRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final AiFeedbackRepository aiFeedbackRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── 피드백 목록 조회 ──────────────────────────────────
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto.FeedbackInfo> getFeedbacks(Long userId, String type) {
        List<AiFeedback> feedbacks = type != null
                ? aiFeedbackRepository.findByUserIdAndFeedbackTypeOrderByCreatedAtDesc(userId, type)
                : aiFeedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return feedbacks.stream()
                .map(FeedbackResponseDto.FeedbackInfo::from)
                .toList();
    }

    // ── 최신 피드백 조회 ──────────────────────────────────
    @Transactional(readOnly = true)
    public FeedbackResponseDto.LatestFeedback getLatestFeedback(Long userId) {
        AiFeedback feedback = aiFeedbackRepository
                .findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElse(null);

        if (feedback == null) return null;
        return FeedbackResponseDto.LatestFeedback.from(feedback);
    }

    // ── 일일 피드백 생성 (Scheduler가 호출) ───────────────
    // AI 연동 전 임시 템플릿으로 생성
    @Transactional
    public void generateDailyFeedback(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            UserStatus status = userStatusRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // 스탯 스냅샷 생성
            Map<String, Object> snapshot = new HashMap<>();
            snapshot.put("level", status.getLevel());
            snapshot.put("experiencePoints", status.getExperiencePoints());
            snapshot.put("gold", status.getGold());
            snapshot.put("streakDays", status.getStreakDays());

            String snapshotJson = objectMapper.writeValueAsString(snapshot);

            // AI 연동 전 임시 피드백 내용
            String content = generateTemplateFeedback(status);

            AiFeedback feedback = AiFeedback.create(user, "DAILY", content, snapshotJson);
            aiFeedbackRepository.save(feedback);

            log.info("[피드백 생성] userId={}, type=DAILY", userId);

        } catch (Exception e) {
            log.error("[피드백 생성 실패] userId={}, error={}", userId, e.getMessage());
        }
    }

    // ── 임시 피드백 템플릿 ────────────────────────────────
    // AI 연동 후 이 메서드를 GeminiAiService 호출로 교체
    private String generateTemplateFeedback(UserStatus status) {
        if (status.getStreakDays() >= 7) {
            return String.format(
                "오늘도 정말 잘하셨어요! 🔥 %d일 연속 달성 중이에요. " +
                "현재 Lv.%d, 이 페이스라면 목표에 빠르게 도달할 수 있어요!",
                status.getStreakDays(), status.getLevel());
        } else if (status.getStreakDays() >= 3) {
            return String.format(
                "좋아요! %d일 연속 달성 중이에요. 🎯 " +
                "Lv.%d까지 올라왔네요. 꾸준히 하면 반드시 됩니다!",
                status.getStreakDays(), status.getLevel());
        } else {
            return String.format(
                "오늘 하루도 수고하셨어요! 현재 Lv.%d이에요. " +
                "매일 조금씩 하다 보면 어느새 목표에 도달해 있을 거예요! 💪",
                status.getLevel());
        }
    }
}
