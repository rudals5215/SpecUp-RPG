package com.specuprpg.domain.quest.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

public class QuestRequestDto {

    // 커스텀 퀘스트 생성 요청
    // 유저가 직접 퀘스트를 만들 때 프론트에서 이 형식으로 보내줘요
    @Getter
    public static class Create {

        @NotBlank(message = "퀘스트 제목을 입력해주세요.")
        @Size(max = 100, message = "퀘스트 제목은 100자 이하로 입력해주세요.")
        private String title;

        private String description;

        // DAILY / WEEKLY / CHALLENGE 중 하나만 허용
        @NotBlank(message = "퀘스트 타입을 입력해주세요.")
        @Pattern(regexp = "DAILY|WEEKLY|CHALLENGE",
                message = "퀘스트 타입은 DAILY, WEEKLY, CHALLENGE 중 하나여야 해요.")
        private String questType;

        @NotBlank(message = "카테고리를 입력해주세요.")
        private String category;

        // 보상 XP와 골드는 0 이상이어야 해요
        @Min(value = 0, message = "보상 XP는 0 이상이어야 해요.")
        private int rewardXp;

        @Min(value = 0, message = "보상 골드는 0 이상이어야 해요.")
        private int rewardGold;

        // 마감일 (없으면 오늘 날짜로 처리)
        private LocalDate dueDate;
    }
}
