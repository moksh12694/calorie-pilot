package com.caloriepilot.api.modules.achievements.dto;

import java.time.LocalDate;

public record StreakResponse(
        Integer currentDays,
        Integer longestDays,
        LocalDate lastGoalDate
) {}
