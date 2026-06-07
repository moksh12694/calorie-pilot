package com.caloriepilot.api.modules.water.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record WaterLogRequest(
        @NotNull LocalDate date,
        @NotNull @Positive Integer amountMl
) {}
