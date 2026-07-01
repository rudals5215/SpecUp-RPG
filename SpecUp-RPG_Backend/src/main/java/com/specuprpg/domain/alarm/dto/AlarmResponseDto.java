package com.specuprpg.domain.alarm.dto;

import com.specuprpg.domain.alarm.entity.AlarmSetting;
import lombok.Builder;
import lombok.Getter;

public class AlarmResponseDto {

    @Getter
    @Builder
    public static class AlarmInfo {
        private boolean morningAlarm;
        private String morningTime;
        private boolean afternoonAlarm;
        private boolean deadlineAlarm;
        private boolean comebackAlarm;
        private boolean levelupAlarm;
        private boolean pushEnabled;

        public static AlarmInfo from(AlarmSetting setting) {
            return AlarmInfo.builder()
                    .morningAlarm(setting.isMorningAlarm())
                    .morningTime(setting.getMorningTime())
                    .afternoonAlarm(setting.isAfternoonAlarm())
                    .deadlineAlarm(setting.isDeadlineAlarm())
                    .comebackAlarm(setting.isComebackAlarm())
                    .levelupAlarm(setting.isLevelupAlarm())
                    .pushEnabled(setting.isPushEnabled())
                    .build();
        }
    }
}
