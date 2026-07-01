package com.specuprpg.domain.alarm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

public class AlarmRequestDto {

    @Getter
    public static class Update {
        private boolean morningAlarm;

        @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$",
                message = "시간 형식이 올바르지 않아요. (예: 08:00)")
        private String morningTime = "08:00";

        private boolean afternoonAlarm;
        private boolean deadlineAlarm;
        private boolean comebackAlarm;
        private boolean levelupAlarm;
        private boolean pushEnabled;
    }
}
