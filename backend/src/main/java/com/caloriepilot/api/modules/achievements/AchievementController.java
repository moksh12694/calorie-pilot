package com.caloriepilot.api.modules.achievements;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.achievements.dto.AchievementResponse;
import com.caloriepilot.api.modules.achievements.dto.StreakResponse;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Achievements")
@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService service;

    @GetMapping
    public ApiResponse<List<AchievementResponse>> list(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.ok(service.listForUser(user.id()));
    }

    @GetMapping("/streak")
    public ApiResponse<StreakResponse> streak(@CurrentUser AuthenticatedUser user) {
        return ApiResponse.ok(service.streakFor(user.id()));
    }
}
