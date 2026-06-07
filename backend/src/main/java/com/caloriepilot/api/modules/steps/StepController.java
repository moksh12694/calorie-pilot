package com.caloriepilot.api.modules.steps;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.steps.dto.StepGoalRequest;
import com.caloriepilot.api.modules.steps.dto.StepLogResponse;
import com.caloriepilot.api.modules.steps.dto.StepSyncRequest;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Steps")
@RestController
@RequestMapping("/steps")
@RequiredArgsConstructor
public class StepController {

    private final StepService stepService;

    @PostMapping("/sync")
    public ApiResponse<StepLogResponse> sync(@CurrentUser AuthenticatedUser user,
                                             @Valid @RequestBody StepSyncRequest req) {
        return ApiResponse.ok(stepService.sync(user.id(), req));
    }

    @GetMapping("/today")
    public ApiResponse<StepLogResponse> today(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.ok(stepService.today(user.id()));
    }

    @GetMapping("/history")
    public ApiResponse<List<StepLogResponse>> history(
            @CurrentUser AuthenticatedUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(stepService.history(user.id(), from, to));
    }

    @GetMapping("/goal")
    public ApiResponse<StepService.StepGoalView> getGoal(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.ok(stepService.getGoal(user.id()));
    }

    @PutMapping("/goal")
    public ApiResponse<StepService.StepGoalView> updateGoal(@CurrentUser AuthenticatedUser user,
                                                            @Valid @RequestBody StepGoalRequest req) {
        return ApiResponse.ok(stepService.updateGoal(user.id(), req));
    }
}
