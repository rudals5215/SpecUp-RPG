package com.specuprpg.domain.feedback.dto;

import com.specuprpg.domain.feedback.entity.AiFeedback;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class FeedbackResponseDto {

    // 피드백 목록 응답
    @Getter
    @Builder
    public static class FeedbackInfo {
        private Long feedbackId;
        private String feedbackType;  // DAILY / WEEKLY / MONTHLY / YEARLY
        private String content;
        private LocalDateTime createdAt;

        public static FeedbackInfo from(AiFeedback feedback) {
            return FeedbackInfo.builder()
                    .feedbackId(feedback.getId())
                    .feedbackType(feedback.getFeedbackType())
                    .content(feedback.getContent())
                    .createdAt(feedback.getCreatedAt())
                    .build();
        }
    }

    // 최신 피드백 응답 (스탯 스냅샷 포함)
    @Getter
    @Builder
    public static class LatestFeedback {
        private Long feedbackId;
        private String feedbackType;
        private String content;
        private String statsSnapshot;
        private LocalDateTime createdAt;

        public static LatestFeedback from(AiFeedback feedback) {
            return LatestFeedback.builder()
                    .feedbackId(feedback.getId())
                    .feedbackType(feedback.getFeedbackType())
                    .content(feedback.getContent())
                    .statsSnapshot(feedback.getStatsSnapshot())
                    .createdAt(feedback.getCreatedAt())
                    .build();
        }
    }
}
