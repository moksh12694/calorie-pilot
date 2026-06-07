package com.caloriepilot.api.modules.food;

import com.caloriepilot.api.common.dto.ApiResponse;
import com.caloriepilot.api.modules.food.dto.*;
import com.caloriepilot.api.security.AuthenticatedUser;
import com.caloriepilot.api.security.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Food")
@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService service;

    // ---- catalog ----
    @GetMapping("/foods")
    public ApiResponse<List<FoodResponse>> search(@CurrentUser AuthenticatedUser user,
                                                  @RequestParam(name = "q") String q,
                                                  @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.ok(service.search(user.id(), q, limit));
    }

    @PostMapping("/foods")
    public ApiResponse<FoodResponse> create(@CurrentUser AuthenticatedUser user,
                                            @Valid @RequestBody FoodCreateRequest req) {
        return ApiResponse.ok(service.createCustomFood(user.id(), req));
    }

    // ---- meal entries ----
    @PostMapping("/meals")
    public ApiResponse<MealEntryResponse> add(@CurrentUser AuthenticatedUser user,
                                              @Valid @RequestBody MealEntryRequest req) {
        return ApiResponse.ok(service.addEntry(user.id(), req));
    }

    @DeleteMapping("/meals/{id}")
    public ApiResponse<Void> delete(@CurrentUser AuthenticatedUser user, @PathVariable Long id) {
        service.deleteEntry(user.id(), id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/meals/daily")
    public ApiResponse<DailySummaryResponse> daily(@CurrentUser AuthenticatedUser user,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(service.daily(user.id(), date));
    }
}
