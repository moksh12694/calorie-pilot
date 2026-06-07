package com.caloriepilot.api.modules.food.dto;

import com.caloriepilot.api.modules.food.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MealEntryRequest(
        @NotNull LocalDate date,
        @NotNull MealType meal,
        @NotNull Long foodId,
        @NotNull @Positive BigDecimal servings
) {}
