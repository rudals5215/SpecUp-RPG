package com.specuprpg.domain.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class AiRequestDto {

    // AI 진단 대화 요청
    // 유저가 메시지를 보낼 때마다 지금까지의 대화 목록도 같이 보내줘요
    // AI가 이전 대화를 기억하게 하기 위해서예요
    @Getter
    public static class DiagnosisChat {

        @NotBlank(message = "메시지를 입력해주세요.")
        private String message;

        // 지금까지 나눈 대화 목록 (AI 컨텍스트 유지용)
        private List<ChatMessage> conversationHistory;

        @Getter
        public static class ChatMessage {
            private String role;    // "user" 또는 "assistant"
            private String content; // 실제 메시지 내용
        }
    }

    // 목표 변경 요청
    @Getter
    public static class CurriculumChange {

        // 변경 타입
        // SWITCH = 목표 완전 전환
        // PAUSE_AND_ADD = 단기 목표 추가 (기존 일시정지)
        // MERGE = 비슷한 목표 병합
        // ADJUST_PACE = 페이스 조정
        @NotBlank(message = "변경 타입을 입력해주세요.")
        @Pattern(regexp = "SWITCH|PAUSE_AND_ADD|MERGE|ADJUST_PACE",
                message = "올바른 변경 타입을 입력해주세요.")
        private String changeType;

        @NotBlank(message = "변경 사유를 입력해주세요.")
        private String reason;

        // 단기 목표 추가 시 원래 커리큘럼 복귀 날짜
        private LocalDate resumeAt;
    }
}
