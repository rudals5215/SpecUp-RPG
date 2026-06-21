package com.specuprpg.domain.ai.controller;

import com.specuprpg.domain.ai.dto.AiRequestDto;
import com.specuprpg.domain.ai.dto.AiResponseDto;
import com.specuprpg.domain.ai.service.AiCurriculumService;
import com.specuprpg.domain.ai.service.AiDiagnosisService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiDiagnosisService aiDiagnosisService;
    private final AiCurriculumService aiCurriculumService;
    private final JwtProvider jwtProvider;

    // ── AI 진단 대화 ──────────────────────────────────────
    // POST /api/ai/diagnosis/chat
    @PostMapping("/diagnosis/chat")
    public ResponseEntity<ApiResponse<AiResponseDto.DiagnosisChatResult>> chat(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AiRequestDto.DiagnosisChat request) {

        Long userId = extractUserId(token);
        AiResponseDto.DiagnosisChatResult response =
                aiDiagnosisService.chat(userId, request);

        String message = response.isDiagnosisComplete()
                ? "커리큘럼이 생성됐어요! 퀘스트를 시작해볼까요? 🚀"
                : "요청이 성공했습니다.";

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    // ── 커리큘럼 조회 ─────────────────────────────────────
    // GET /api/ai/curriculum
    @GetMapping("/curriculum")
    public ResponseEntity<ApiResponse<AiResponseDto.CurriculumDetail>> getCurriculum(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                aiCurriculumService.getCurriculum(userId)));
    }

    // ── 목표 변경 ─────────────────────────────────────────
    // POST /api/ai/curriculum/change
    @PostMapping("/curriculum/change")
    public ResponseEntity<ApiResponse<AiResponseDto.CurriculumChangeResult>> changeCurriculum(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AiRequestDto.CurriculumChange request) {

        Long userId = extractUserId(token);
        AiResponseDto.CurriculumChangeResult response =
                aiCurriculumService.changeCurriculum(userId, request);

        return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
