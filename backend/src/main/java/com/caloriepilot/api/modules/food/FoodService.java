package com.caloriepilot.api.modules.food;

import com.caloriepilot.api.common.exception.ApiException;
import com.caloriepilot.api.modules.achievements.AchievementService;
import com.caloriepilot.api.modules.food.dto.*;
import com.caloriepilot.api.modules.user.UserGoal;
import com.caloriepilot.api.modules.user.UserGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final MealEntryRepository mealEntryRepository;
    private final UserGoalRepository userGoalRepository;
    private final AchievementService achievementService;

    @Transactional(readOnly = true)
    public List<FoodResponse> search(UUID userId, String q, int limit) {
        if (q == null || q.isBlank()) return List.of();
        return foodRepository.search(q.trim(), userId, PageRequest.of(0, Math.min(limit, 50)))
                .stream().map(this::toFood).toList();
    }

    @Transactional
    public FoodResponse createCustomFood(UUID userId, FoodCreateRequest req) {
        Food food = Food.builder()
                .name(req.name())
                .brand(req.brand())
                .servingSizeG(req.servingSizeG())
                .calories(req.calories())
                .proteinG(orZero(req.proteinG()))
                .carbsG(orZero(req.carbsG()))
                .fatG(orZero(req.fatG()))
                .fiberG(orZero(req.fiberG()))
                .isPublic(false)
                .createdBy(userId)
                .build();
        return toFood(foodRepository.save(food));
    }

    @Transactional
    public MealEntryResponse addEntry(UUID userId, MealEntryRequest req) {
        Food food = foodRepository.findById(req.foodId())
                .orElseThrow(() -> ApiException.notFound("Food"));
        if (!Boolean.TRUE.equals(food.getIsPublic()) && !userId.equals(food.getCreatedBy())) {
            throw ApiException.forbidden("Cannot access this food");
        }

        BigDecimal servings = req.servings();
        MealEntry entry = MealEntry.builder()
                .userId(userId)
                .foodId(food.getId())
                .logDate(req.date())
                .meal(req.meal())
                .servings(servings)
                .calories(scale(food.getCalories(), servings))
                .proteinG(scale(food.getProteinG(), servings))
                .carbsG(scale(food.getCarbsG(), servings))
                .fatG(scale(food.getFatG(), servings))
                .build();
        entry = mealEntryRepository.save(entry);
        achievementService.onFirstMeal(userId);
        return toEntry(entry, food.getName());
    }

    @Transactional
    public void deleteEntry(UUID userId, Long entryId) {
        MealEntry entry = mealEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> ApiException.notFound("Meal entry"));
        mealEntryRepository.delete(entry);
    }

    @Transactional(readOnly = true)
    public DailySummaryResponse daily(UUID userId, LocalDate date) {
        List<MealEntry> entries = mealEntryRepository.findByUserIdAndLogDateOrderByMealAscIdAsc(userId, date);
        MealEntryRepository.DailyTotals totals = mealEntryRepository.sumForDay(userId, date);
        UserGoal goal = userGoalRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Goals"));

        List<Long> foodIds = entries.stream().map(MealEntry::getFoodId).distinct().toList();
        var nameById = foodRepository.findAllById(foodIds).stream()
                .collect(java.util.stream.Collectors.toMap(Food::getId, Food::getName));

        List<MealEntryResponse> entryResps = entries.stream()
                .map(e -> toEntry(e, nameById.getOrDefault(e.getFoodId(), "Unknown")))
                .toList();

        return new DailySummaryResponse(
                date,
                totals.calories(), totals.protein(), totals.carbs(), totals.fat(),
                goal.getDailyCalorieGoal(), goal.getDailyProteinG(), goal.getDailyCarbsG(), goal.getDailyFatG(),
                entryResps
        );
    }

    private FoodResponse toFood(Food f) {
        return new FoodResponse(f.getId(), f.getName(), f.getBrand(),
                f.getServingSizeG(), f.getCalories(),
                f.getProteinG(), f.getCarbsG(), f.getFatG(), f.getFiberG());
    }

    private MealEntryResponse toEntry(MealEntry e, String foodName) {
        return new MealEntryResponse(e.getId(), e.getLogDate(), e.getMeal(),
                e.getFoodId(), foodName, e.getServings(),
                e.getCalories(), e.getProteinG(), e.getCarbsG(), e.getFatG());
    }

    private static BigDecimal scale(BigDecimal base, BigDecimal servings) {
        return base.multiply(servings).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal orZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
