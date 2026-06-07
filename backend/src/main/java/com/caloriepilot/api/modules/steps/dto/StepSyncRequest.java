package com.caloriepilot.api.modules.steps.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StepSyncRequest(
        @NotNull LocalDate date,
        @NotNull @PositiveOrZero Integer steps,
        BigDecimal distanceM,
        BigDecimal calories
) {}
