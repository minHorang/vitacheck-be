package com.vitacheck.dto;

import com.vitacheck.domain.RoutineDayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineRegisterResponseDto {

    private Long notificationRoutineId;

    private Long supplementId;

    private List<RoutineDayOfWeek> daysOfWeek;

    private List<LocalTime> times;
}
