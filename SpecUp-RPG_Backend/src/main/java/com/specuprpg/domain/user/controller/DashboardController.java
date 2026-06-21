package com.specuprpg.domain.user.controller;

import com.specuprpg.domain.user.dto.DashboardResponseDto;
import com.specuprpg.domain.user.service.DashboardService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtProvider jwtProvider;

    // ── 대시보드 통합 조회 ────────────────────────────────
    // GET /api/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponseDto.Dashboard>> getDashboard(
            @RequestHeader("Authorization") String token) {

        Long userId = jwtProvider.getUserId(token.substring(7));
        DashboardResponseDto.Dashboard response = dashboardService.getDashboard(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
