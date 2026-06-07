package com.caloriepilot.api.modules.water.dto;

import java.time.LocalDate;
import java.util.List;

public record WaterDayResponse(
        LocalDate date,
        Integer totalMl,
        Integer goalMl,
        Integer progressPct,
        List<WaterEntry> entries
) {}
