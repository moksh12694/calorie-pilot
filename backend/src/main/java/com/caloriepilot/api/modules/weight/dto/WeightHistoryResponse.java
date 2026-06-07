package com.caloriepilot.api.modules.weight.dto;

import java.math.BigDecimal;
import java.util.List;

public record WeightHistoryResponse(
        BigDecimal latestKg,
        BigDecimal earliestKg,
        BigDecimal totalDeltaKg,
        BigDecimal targetKg,
        List<WeightEntry> entries
) {}
