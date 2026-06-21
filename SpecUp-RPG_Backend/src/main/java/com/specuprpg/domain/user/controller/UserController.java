package com.specuprpg.domain.user.controller;

import com.specuprpg.domain.user.dto.UserRequestDto;
import com.specuprpg.domain.user.dto.UserResponseDto;
import com.specuprpg.domain.user.service.UserService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    // ── 내 정보 조회 ──────────────────────────────────────
    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.MyInfo>> getMyInfo(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        UserResponseDto.MyInfo response = userService.getMyInfo(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── 닉네임 수정 ───────────────────────────────────────
    // PATCH /api/users/me/nickname
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<UserResponseDto.UpdateNickname>> updateNickname(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserRequestDto.UpdateNickname request) {

        Long userId = extractUserId(token);
        UserResponseDto.UpdateNickname response =
                userService.updateNickname(userId, request.getNickname());

        return ResponseEntity.ok(ApiResponse.success(response, "닉네임이 변경됐어요."));
    }

    // ── AI 토큰 현황 조회 ─────────────────────────────────
    // GET /api/users/me/ai-token
    @GetMapping("/me/ai-token")
    public ResponseEntity<ApiResponse<UserResponseDto.AiTokenInfo>> getAiTokenInfo(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        UserResponseDto.AiTokenInfo response = userService.getAiTokenInfo(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── 공통: 토큰에서 userId 추출 ────────────────────────
    private Long extractUserId(String bearerToken) {
        String token = bearerToken.substring(7); // "Bearer " 제거
        return jwtProvider.getUserId(token);
    }
}
