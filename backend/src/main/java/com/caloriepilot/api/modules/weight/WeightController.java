package com.caloriepilot.api.modules.weight;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.weight.dto.WeightEntry;
import com.caloriepilot.api.modules.weight.dto.WeightHistoryResponse;
import com.caloriepilot.api.modules.weight.dto.WeightLogRequest;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Weight")
@RestController
@RequestMapping("/weight")
@RequiredArgsConstructor
public class WeightController {

    private final WeightService service;

    @PostMapping
    public ApiResponse<WeightEntry> log(@CurrentUser AuthenticatedUser user,
                                        @Valid @RequestBody WeightLogRequest req) {
        return ApiResponse.ok(service.log(user.id(), req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@CurrentUser AuthenticatedUser user, @PathVariable Long id) {
        service.delete(user.id(), id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/history")
    public ApiResponse<WeightHistoryResponse> history(
            @CurrentUser AuthenticatedUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(service.history(user.id(), from, to));
    }
}
