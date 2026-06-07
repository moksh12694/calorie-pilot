package com.caloriepilot.api.modules.analytics;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.analytics.dto.AnalyticsRange;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Analytics")
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/daily")
    public ApiResponse<AnalyticsRange> daily(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(service.daily(user.id(), date != null ? date : LocalDate.now()));
    }

    @GetMapping("/weekly")
    public ApiResponse<AnalyticsRange> weekly(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate anchor) {
        return ApiResponse.ok(service.weekly(user.id(), anchor != null ? anchor : LocalDate.now()));
    }

    @GetMapping("/monthly")
    public ApiResponse<AnalyticsRange> monthly(
            @CurrentUser AuthenticatedUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate anchor) {
        return ApiResponse.ok(service.monthly(user.id(), anchor != null ? anchor : LocalDate.now()));
    }
}
