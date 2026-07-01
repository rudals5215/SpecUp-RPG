package com.specuprpg.domain.pet.service;

import com.specuprpg.domain.pet.dto.PetRequestDto;
import com.specuprpg.domain.pet.dto.PetResponseDto;
import com.specuprpg.domain.pet.entity.Pet;
import com.specuprpg.domain.pet.repository.PetRepository;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // ── 내 펫 조회 ────────────────────────────────────────
    @Transactional(readOnly = true)
    public PetResponseDto.PetInfo getMyPet(Long userId) {
        Pet pet = getPetByUserId(userId);
        return PetResponseDto.PetInfo.from(pet);
    }

    // ── 펫 이름 변경 ──────────────────────────────────────
    @Transactional
    public PetResponseDto.UpdateName updatePetName(Long userId, PetRequestDto.UpdateName request) {
        Pet pet = getPetByUserId(userId);
        pet.updateName(request.getName());

        log.info("[펫 이름 변경] userId={}, name={}", userId, request.getName());
        return PetResponseDto.UpdateName.of(request.getName());
    }

    // ── AI 펫 대화 ────────────────────────────────────────
    // AI 연동 전 임시 응답 (나중에 GeminiAiService 연동)
    @Transactional
    public PetResponseDto.ChatResult chatWithPet(Long userId, String message) {
        Pet pet = getPetByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // AI 토큰 차감
        if (!user.useAiToken(1)) {
            throw new CustomException(ErrorCode.AI_TOKEN_INSUFFICIENT);
        }

        // 펫 상태에 따라 다른 응답 생성 (AI 연동 전 임시)
        String reply = generatePetReply(pet, message);

        log.info("[펫 대화] userId={}, petName={}", userId, pet.getName());

        return PetResponseDto.ChatResult.builder()
                .reply(reply)
                .petHunger(pet.getHunger())
                .remainingAiToken(user.getAiToken())
                .build();
    }

    // ── 퀘스트 완료 시 펫 경험치 지급 (QuestService에서 호출) ──
    @Transactional
    public void rewardPet(Long userId, int expAmount) {
        petRepository.findByUserId(userId).ifPresent(pet -> {
            pet.gainExp(expAmount);
            log.info("[펫 경험치] userId={}, exp+={}", userId, expAmount);
        });
    }

    // ── 임시 펫 대화 응답 생성 ────────────────────────────
    // AI 연동 후 이 메서드를 GeminiAiService 호출로 교체
    private String generatePetReply(Pet pet, String message) {
        if (pet.isHungry()) {
            return pet.getName() + "이(가) 배가 너무 고파요 😢 퀘스트를 완료해서 밥을 주세요!";
        }
        return switch (pet.getStatus()) {
            case "EGG" -> "🥚 알이 살짝 흔들렸어요! 퀘스트를 더 완료하면 부화할 것 같아요!";
            case "BABY" -> pet.getName() + ": 냐옹~ 오늘도 퀘스트 열심히 했어요? 🐣";
            case "ADULT" -> pet.getName() + ": 오늘 퀘스트 잘 하고 있어요! 같이 성장해봐요 💪";
            case "EVOLVED" -> pet.getName() + ": 저도 최강이 됐어요! 당신도 할 수 있어요! ⭐";
            default -> "...";
        };
    }

    // ── 공통 유틸 ─────────────────────────────────────────
    private Pet getPetByUserId(Long userId) {
        return petRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
    }
}
