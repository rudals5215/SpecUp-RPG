package com.specuprpg.domain.auth.dto;

import com.specuprpg.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AuthResponseDto {

    // ── 회원가입 응답 ─────────────────────────────────────
    @Getter
    @Builder
    public static class Register {
        private Long id;
        private String email;
        private String nickname;
        private LocalDateTime createdAt;

        public static Register from(User user) {
            return Register.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    // ── 로그인 응답 ───────────────────────────────────────
    @Getter
    @Builder
    public static class Login {
        private String accessToken;
        private String tokenType;
        private UserInfo user;

        @Getter
        @Builder
        public static class UserInfo {
            private Long id;
            private String email;
            private String nickname;
            private String role;
            private int aiToken;
            private int aiTokenMax;
            private LocalDateTime aiTokenResetAt;
        }

        public static Login of(String token, User user) {
            return Login.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .user(UserInfo.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .nickname(user.getNickname())
                            .role(user.getRole())
                            .aiToken(user.getAiToken())
                            .aiTokenMax(user.getAiTokenMax())
                            .aiTokenResetAt(user.getAiTokenResetAt())
                            .build())
                    .build();
        }
    }
}
