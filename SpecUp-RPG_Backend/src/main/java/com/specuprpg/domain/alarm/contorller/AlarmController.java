package com.specuprpg.domain.alarm.controller;

import com.specuprpg.domain.alarm.dto.AlarmRequestDto;
import com.specuprpg.domain.alarm.dto.AlarmResponseDto;
import com.specuprpg.domain.alarm.service.AlarmService;
import com.specuprpg.global.response.ApiResponse;
import com.specuprpg.global.security.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final JwtProvider jwtProvider;

    // GET /api/alarms/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AlarmResponseDto.AlarmInfo>> getAlarmSetting(
            @RequestHeader("Authorization") String token) {

        Long userId = extractUserId(token);
        return ResponseEntity.ok(ApiResponse.success(
                alarmService.getAlarmSetting(userId)));
    }

    // PATCH /api/alarms/me
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<AlarmResponseDto.AlarmInfo>> updateAlarmSetting(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AlarmRequestDto.Update request) {

        Long userId = extractUserId(token);
        AlarmResponseDto.AlarmInfo response = alarmService.updateAlarmSetting(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "알람 설정이 저장됐어요."));
    }

    private Long extractUserId(String bearerToken) {
        return jwtProvider.getUserId(bearerToken.substring(7));
    }
}
