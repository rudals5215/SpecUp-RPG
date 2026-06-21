package com.specuprpg.domain.auth.service;

import com.specuprpg.domain.auth.dto.AuthRequestDto;
import com.specuprpg.domain.auth.dto.AuthResponseDto;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import com.specuprpg.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // ── 회원가입 ──────────────────────────────────────────
    @Transactional
    public AuthResponseDto.Register register(AuthRequestDto.Register request) {

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 유저 생성
        User user = User.create(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );
        userRepository.save(user);

        // 4. 유저 스탯 자동 생성 (Lv.1, XP 0, 골드 0)
        UserStatus userStatus = UserStatus.createDefault(user);
        userStatusRepository.save(userStatus);

        log.info("[회원가입] email={}, nickname={}", user.getEmail(), user.getNickname());

        return AuthResponseDto.Register.from(user);
    }

    // ── 로그인 ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AuthResponseDto.Login login(AuthRequestDto.Login request) {

        // 1. 이메일로 유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. JWT 토큰 발급
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());

        log.info("[로그인] email={}", user.getEmail());

        return AuthResponseDto.Login.of(token, user);
    }
}
