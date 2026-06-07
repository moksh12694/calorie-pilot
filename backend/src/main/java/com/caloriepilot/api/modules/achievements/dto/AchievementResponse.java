package com.caloriepilot.api.modules.achievements.dto;

import java.time.Instant;

public record AchievementResponse(
        Long id,
        String code,
        String title,
        String description,
        String icon,
        Integer threshold,
        boolean earned,
        Instant earnedAt
) {}
