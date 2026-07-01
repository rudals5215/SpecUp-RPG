package com.specuprpg.domain.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class PetRequestDto {

    @Getter
    public static class UpdateName {
        @NotBlank(message = "펫 이름을 입력해주세요.")
        @Size(min = 1, max = 10, message = "펫 이름은 1자 이상 10자 이하로 입력해주세요.")
        private String name;
    }
}
