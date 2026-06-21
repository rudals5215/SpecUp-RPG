package com.specuprpg.domain.auth.controller;

import com.specuprpg.domain.auth.dto.AuthRequestDto;
import com.specuprpg.domain.auth.dto.AuthResponseDto;
import com.specuprpg.domain.auth.service.AuthService;
import com.specuprpg.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── 회원가입 ──────────────────────────────────────────
    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto.Register>> register(
            @Valid @RequestBody AuthRequestDto.Register request) {

        AuthResponseDto.Register response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입이 완료됐어요! AI 진단을 시작해볼까요?"));
    }

    // ── 로그인 ────────────────────────────────────────────
    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto.Login>> login(
            @Valid @RequestBody AuthRequestDto.Login request) {

        AuthResponseDto.Login response = authService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "로그인 성공!"));
    }

    // ── 로그아웃 ──────────────────────────────────────────
    // POST /api/auth/logout
    // JWT 방식은 서버에 상태를 저장하지 않아요
    // 실제 토큰 무효화는 추후 Redis 블랙리스트로 확장 가능
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity
                .ok(ApiResponse.successMessage("로그아웃됐어요."));
    }
}
