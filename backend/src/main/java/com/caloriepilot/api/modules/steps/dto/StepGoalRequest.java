package com.caloriepilot.api.modules.steps.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StepGoalRequest(
        @NotNull @Min(1) Integer dailyStepGoal
) {}
