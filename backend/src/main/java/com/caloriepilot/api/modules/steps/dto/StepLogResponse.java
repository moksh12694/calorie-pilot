package com.caloriepilot.api.modules.steps.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StepLogResponse(
        LocalDate date,
        Integer steps,
        BigDecimal distanceM,
        BigDecimal calories,
        Integer goal,
        Integer progressPct,
        boolean goalMet
) {}
