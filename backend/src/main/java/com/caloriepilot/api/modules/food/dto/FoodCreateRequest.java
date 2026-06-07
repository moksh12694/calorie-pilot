package com.caloriepilot.api.modules.food.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record FoodCreateRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 120) String brand,
        @NotNull @Positive BigDecimal servingSizeG,
        @NotNull @PositiveOrZero BigDecimal calories,
        @PositiveOrZero BigDecimal proteinG,
        @PositiveOrZero BigDecimal carbsG,
        @PositiveOrZero BigDecimal fatG,
        @PositiveOrZero BigDecimal fiberG
) {}
