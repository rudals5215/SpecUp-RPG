package com.specuprpg.domain.quest.controller;

import com.specuprpg.domain.quest.dto.QuestRequestDto;
import com.specuprpg.domain.quest.dto.QuestResponseDto;
import com.specuprpg.domain.quest.service.QuestService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;
    private final JwtProvider jwtProvider;

    // ── 오늘의 퀘스트 목록 조회 ──────────────────────────
    // GET /api/quests/today
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<QuestResponseDto.TodayQuests>> getTodayQuests(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(questService.getTodayQuests(userId)));
    }

    // ── 주간 퀘스트 목록 조회 ─────────────────────────────
    // GET /api/quests/weekly
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<List<QuestResponseDto.QuestInfo>>> getWeeklyQuests(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(questService.getWeeklyQuests(userId)));
    }

    // ── 커스텀 퀘스트 생성 ────────────────────────────────
    // POST /api/quests
    @PostMapping
    public ResponseEntity<ApiResponse<QuestResponseDto.CreateResult>> createQuest(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody QuestRequestDto.Create request) {

        Long userId = extractUserId(token);
        QuestResponseDto.CreateResult response = questService.createQuest(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "퀘스트가 추가됐어요! 파이팅 🔥"));
    }

    // ── 퀘스트 완료 처리 ⭐ ────────────────────────────────
    // POST /api/quests/{userQuestId}/complete
    @PostMapping("/{userQuestId}/complete")
    public ResponseEntity<ApiResponse<QuestResponseDto.CompleteResult>> completeQuest(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userQuestId) {

        Long userId = extractUserId(token);
        QuestResponseDto.CompleteResult response = questService.completeQuest(userId, userQuestId);

        // 레벨업 여부에 따라 메시지 다르게
        String message = response.getUserStatus().isLevelUp()
                ? String.format("레벨업! Lv.%d → Lv.%d 🎊",
                    response.getUserStatus().getPreviousLevel(),
                    response.getUserStatus().getLevel())
                : String.format("퀘스트 완료! +%d XP, +%d 골드 획득 🎉",
                    response.getXpGained(), response.getGoldGained());

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    // ── 퀘스트 삭제 ───────────────────────────────────────
    // DELETE /api/quests/{userQuestId}
    @DeleteMapping("/{userQuestId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuest(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userQuestId) {

        Long userId = extractUserId(token);
        questService.deleteQuest(userId, userQuestId);

        return ResponseEntity.ok(ApiResponse.successMessage("퀘스트가 삭제됐어요."));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
