package com.specuprpg.domain.alarm.repository;

import com.specuprpg.domain.alarm.entity.AlarmSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {

    // 유저 ID로 알람 설정 조회
    Optional<AlarmSetting> findByUserId(Long userId);
}
