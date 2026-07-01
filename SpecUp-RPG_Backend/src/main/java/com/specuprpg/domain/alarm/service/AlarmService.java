package com.specuprpg.domain.alarm.service;

import com.specuprpg.domain.alarm.dto.AlarmRequestDto;
import com.specuprpg.domain.alarm.dto.AlarmResponseDto;
import com.specuprpg.domain.alarm.entity.AlarmSetting;
import com.specuprpg.domain.alarm.repository.AlarmSettingRepository;
import com.specuprpg.global.exception.CustomException;
import com.specuprpg.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmSettingRepository alarmSettingRepository;

    // ── 알람 설정 조회 ────────────────────────────────────
    @Transactional(readOnly = true)
    public AlarmResponseDto.AlarmInfo getAlarmSetting(Long userId) {
        AlarmSetting setting = alarmSettingRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return AlarmResponseDto.AlarmInfo.from(setting);
    }

    // ── 알람 설정 수정 ────────────────────────────────────
    @Transactional
    public AlarmResponseDto.AlarmInfo updateAlarmSetting(Long userId,
                                                          AlarmRequestDto.Update request) {
        AlarmSetting setting = alarmSettingRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        setting.update(
                request.isMorningAlarm(),
                request.getMorningTime(),
                request.isAfternoonAlarm(),
                request.isDeadlineAlarm(),
                request.isComebackAlarm(),
                request.isLevelupAlarm(),
                request.isPushEnabled()
        );

        log.info("[알람 설정 변경] userId={}", userId);
        return AlarmResponseDto.AlarmInfo.from(setting);
    }
}
