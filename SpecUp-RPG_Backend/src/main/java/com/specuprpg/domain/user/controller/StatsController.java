package com.specuprpg.domain.user.controller;

import com.specuprpg.domain.user.dto.StatsResponseDto;
import com.specuprpg.domain.user.service.StatsService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final JwtProvider jwtProvider;

    // ── 주간 달성 통계 ────────────────────────────────────
    // GET /api/stats/weekly
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<StatsResponseDto.WeeklyStats>> getWeeklyStats(
            @RequestHeader("Authorization") String token) {

        Long userId = jwtProvider.getUserId(token.substring(7));
        StatsResponseDto.WeeklyStats response = statsService.getWeeklyStats(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
