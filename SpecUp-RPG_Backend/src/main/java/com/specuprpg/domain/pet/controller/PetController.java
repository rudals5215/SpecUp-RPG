package com.specuprpg.domain.pet.controller;

import com.specuprpg.domain.pet.dto.PetRequestDto;
import com.specuprpg.domain.pet.dto.PetResponseDto;
import com.specuprpg.domain.pet.service.PetService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final JwtProvider jwtProvider;

    // ── 내 펫 조회 ────────────────────────────────────────
    // GET /api/pets/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PetResponseDto.PetInfo>> getMyPet(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(petService.getMyPet(userId)));
    }

    // ── 펫 이름 변경 ──────────────────────────────────────
    // PATCH /api/pets/me/name
    @PatchMapping("/me/name")
    public ResponseEntity<ApiResponse<PetResponseDto.UpdateName>> updatePetName(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody PetRequestDto.UpdateName request) {

        Long userId = extractUserId(token);
        PetResponseDto.UpdateName response = petService.updatePetName(userId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "펫 이름이 바뀌었어요!"));
    }

    // ── AI 펫 대화 ────────────────────────────────────────
    // POST /api/pets/me/chat
    @PostMapping("/me/chat")
    public ResponseEntity<ApiResponse<PetResponseDto.ChatResult>> chatWithPet(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {

        Long userId = extractUserId(token);
        String message = body.getOrDefault("message", "");

        PetResponseDto.ChatResult response = petService.chatWithPet(userId, message);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
