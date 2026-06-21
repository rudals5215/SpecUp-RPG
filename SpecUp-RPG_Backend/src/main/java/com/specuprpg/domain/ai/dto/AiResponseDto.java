package com.specuprpg.domain.ai.dto;

import com.specuprpg.domain.ai.entity.AiCurriculum;
import com.specuprpg.domain.ai.entity.AiDiagnosis;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class AiResponseDto {

    // AI 진단 대화 응답
    @Getter
    @Builder
    public static class DiagnosisChatResult {
        private String reply;               // AI가 한 말
        private boolean isDiagnosisComplete; // 진단 완료 여부
        private DiagnosisInfo diagnosis;    // 진단 완료 시에만 채워짐
        private CurriculumInfo curriculum;  // 진단 완료 시에만 채워짐
        private int remainingAiToken;       // 남은 AI 토큰

        @Getter
        @Builder
        public static class DiagnosisInfo {
            private Long diagnosisId;
            private String assessedLevel;
            private String goalSummary;
            private double dailyHours;
        }

        @Getter
        @Builder
        public static class CurriculumInfo {
            private Long curriculumId;
            private String title;
            private int totalWeeks;
        }
    }

    // 커리큘럼 조회 응답
    @Getter
    @Builder
    public static class CurriculumDetail {
        private ActiveCurriculum activeCurriculum;
        private List<PausedCurriculum> pausedCurriculums;

        @Getter
        @Builder
        public static class ActiveCurriculum {
            private Long curriculumId;
            private String title;
            private int totalWeeks;
            private boolean isActive;
            private String roadmapJson; // 주차별 퀘스트 목록 JSON

            public static ActiveCurriculum from(AiCurriculum curriculum) {
                return ActiveCurriculum.builder()
                        .curriculumId(curriculum.getId())
                        .title(curriculum.getTitle())
                        .totalWeeks(curriculum.getTotalWeeks())
                        .isActive(curriculum.isActive())
                        .roadmapJson(curriculum.getRoadmapJson())
                        .build();
            }
        }

        @Getter
        @Builder
        public static class PausedCurriculum {
            private Long curriculumId;
            private String title;
            private LocalDate resumeAt;

            public static PausedCurriculum from(AiCurriculum curriculum) {
                return PausedCurriculum.builder()
                        .curriculumId(curriculum.getId())
                        .title(curriculum.getTitle())
                        .resumeAt(curriculum.getResumeAt())
                        .build();
            }
        }
    }

    // 목표 변경 응답
    @Getter
    @Builder
    public static class CurriculumChangeResult {
        private PausedInfo pausedCurriculum;  // 일시정지된 커리큘럼 (있을 때만)
        private NewInfo newCurriculum;         // 새로 생성된 커리큘럼 (있을 때만)
        private String message;

        @Getter
        @Builder
        public static class PausedInfo {
            private Long curriculumId;
            private String title;
            private LocalDate resumeAt;
        }

        @Getter
        @Builder
        public static class NewInfo {
            private Long curriculumId;
            private String title;
            private int totalWeeks;
            private Long parentId;
        }
    }
}
