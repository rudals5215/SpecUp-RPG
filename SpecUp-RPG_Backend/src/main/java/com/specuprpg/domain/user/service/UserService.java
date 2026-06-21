package com.specuprpg.domain.user.service;

import com.specuprpg.domain.user.dto.UserResponseDto;
import com.specuprpg.domain.user.entity.User;
import com.specuprpg.domain.user.entity.UserStatus;
import com.specuprpg.domain.user.entity.UserJobMastery;
import com.specuprpg.domain.user.repository.UserRepository;
import com.specuprpg.domain.user.repository.UserStatusRepository;
import com.specuprpg.domain.user.repository.UserJobMasteryRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserJobMasteryRepository userJobMasteryRepository;

    // ── 내 정보 조회 ──────────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDto.MyInfo getMyInfo(Long userId) {
        User user = getUserById(userId);
        UserStatus status = getStatusByUserId(userId);
        List<UserJobMastery> masteries = userJobMasteryRepository.findAllByUserId(userId);

        return UserResponseDto.MyInfo.of(user, status, masteries);
    }

    // ── 닉네임 수정 ───────────────────────────────────────
    @Transactional
    public UserResponseDto.UpdateNickname updateNickname(Long userId, String nickname) {
        User user = getUserById(userId);
        user.updateNickname(nickname);

        log.info("[닉네임 수정] userId={}, nickname={}", userId, nickname);
        return UserResponseDto.UpdateNickname.of(nickname);
    }

    // ── AI 토큰 현황 조회 ─────────────────────────────────
    @Transactional(readOnly = true)
    public UserResponseDto.AiTokenInfo getAiTokenInfo(Long userId) {
        User user = getUserById(userId);

        // 다음 충전까지 남은 시간 계산
        long resetInHours = ChronoUnit.HOURS.between(
                LocalDateTime.now(), user.getAiTokenResetAt());
        resetInHours = Math.max(0, resetInHours); // 음수 방지

        return UserResponseDto.AiTokenInfo.of(user, resetInHours);
    }

    // ── 공통 유틸 ─────────────────────────────────────────
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private UserStatus getStatusByUserId(Long userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
