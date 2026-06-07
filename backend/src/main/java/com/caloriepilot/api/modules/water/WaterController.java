package com.caloriepilot.api.modules.water;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.water.dto.WaterDayResponse;
import com.caloriepilot.api.modules.water.dto.WaterEntry;
import com.caloriepilot.api.modules.water.dto.WaterLogRequest;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Water")
@RestController
@RequestMapping("/water")
@RequiredArgsConstructor
public class WaterController {

    private final WaterService service;

    @PostMapping
    public ApiResponse<WaterEntry> log(@CurrentUser AuthenticatedUser user,
                                       @Valid @RequestBody WaterLogRequest req) {
        return ApiResponse.ok(service.log(user.id(), req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@CurrentUser AuthenticatedUser user, @PathVariable Long id) {
        service.deleteEntry(user.id(), id);
        return ApiResponse.ok(null);
    }

    @GetMapping
    public ApiResponse<WaterDayResponse> day(@CurrentUser AuthenticatedUser user,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(service.day(user.id(), date));
    }
}
