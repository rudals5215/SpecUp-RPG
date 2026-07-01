package com.specuprpg.domain.achievement.controller;

import com.specuprpg.domain.achievement.dto.AchievementResponseDto;
import com.specuprpg.domain.achievement.service.AchievementService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final JwtProvider jwtProvider;

    // ── 내 칭호 목록 조회 ─────────────────────────────────
    // GET /api/achievements/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AchievementResponseDto.MyAchievements>> getMyAchievements(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                achievementService.getMyAchievements(userId)));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
