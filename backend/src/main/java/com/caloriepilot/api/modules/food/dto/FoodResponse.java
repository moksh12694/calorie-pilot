package com.caloriepilot.api.modules.food.dto;

import java.math.BigDecimal;

public record FoodResponse(
        Long id,
        String name,
        String brand,
        BigDecimal servingSizeG,
        BigDecimal calories,
        BigDecimal proteinG,
        BigDecimal carbsG,
        BigDecimal fatG,
        BigDecimal fiberG
) {}
