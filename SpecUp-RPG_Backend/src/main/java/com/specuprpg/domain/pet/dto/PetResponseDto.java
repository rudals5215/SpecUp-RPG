package com.specuprpg.domain.pet.dto;

import com.specuprpg.domain.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PetResponseDto {

    // 펫 상세 정보 응답
    @Getter
    @Builder
    public static class PetInfo {
        private Long petId;
        private String name;
        private String petType;
        private int level;
        private int exp;
        private int hunger;
        private String status;
        private String condition;
        private LocalDateTime evolvedAt;
        private boolean canEvolve;
        private String evolveCondition;

        public static PetInfo from(Pet pet) {
            return PetInfo.builder()
                    .petId(pet.getId())
                    .name(pet.getName())
                    .petType(pet.getPetType())
                    .level(pet.getLevel())
                    .exp(pet.getExp())
                    .hunger(pet.getHunger())
                    .status(pet.getStatus())
                    .condition(pet.getCondition())
                    .evolvedAt(pet.getEvolvedAt())
                    .canEvolve(pet.canEvolve())
                    .evolveCondition(pet.getEvolveCondition())
                    .build();
        }
    }

    // 펫 이름 변경 응답
    @Getter
    @Builder
    public static class UpdateName {
        private String name;

        public static UpdateName of(String name) {
            return UpdateName.builder().name(name).build();
        }
    }

    // AI 펫 대화 응답
    @Getter
    @Builder
    public static class ChatResult {
        private String reply;
        private int petHunger;
        private int remainingAiToken;
    }
}
