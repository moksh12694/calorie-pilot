package com.caloriepilot.api.modules.achievements;

import com.caloriepilot.api.modules.achievements.dto.AchievementResponse;
import com.caloriepilot.api.modules.achievements.dto.StreakResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final StreakRepository streakRepository;

    /**
     * Called from StepService.sync after a step log is saved. Updates the streak and grants
     * any newly-earned achievements.
     */
    @Transactional
    public void onStepUpdate(UUID userId, LocalDate date, int steps, int dailyStepGoal) {
        boolean goalMet = steps >= dailyStepGoal;

        Streak streak = streakRepository.findById(userId)
                .orElseGet(() -> Streak.builder().userId(userId).build());

        // Streak logic — only advance on the latest day; reset if we skipped a day.
        if (goalMet) {
            LocalDate prev = streak.getLastGoalDate();
            if (prev == null || date.isAfter(prev)) {
                if (prev == null || date.minusDays(1).isAfter(prev)) {
                    // gap (or first time) → reset to 1
                    streak.setCurrentDays(1);
                } else if (date.minusDays(1).isEqual(prev)) {
                    streak.setCurrentDays(streak.getCurrentDays() + 1);
                }
                streak.setLastGoalDate(date);
                if (streak.getCurrentDays() > streak.getLongestDays()) {
                    streak.setLongestDays(streak.getCurrentDays());
                }
                streakRepository.save(streak);
            }
        }

        // Achievement grants
        grant(userId, "FIRST_STEP");                       // any step sync
        if (steps >= 10_000) grant(userId, "STEPS_10K");
        if (steps >= 15_000) grant(userId, "STEPS_15K");
        if (streak.getCurrentDays() >= 3)  grant(userId, "STREAK_3");
        if (streak.getCurrentDays() >= 7)  grant(userId, "STREAK_7");
        if (streak.getCurrentDays() >= 30) grant(userId, "STREAK_30");
    }

    @Transactional
    public void onWaterGoalMet(UUID userId) { grant(userId, "WATER_HYDRATED"); }

    @Transactional
    public void onFirstMeal(UUID userId)    { grant(userId, "FIRST_MEAL"); }

    @Transactional
    public void onFirstWeight(UUID userId)  { grant(userId, "WEIGHT_FIRST_LOG"); }

    /** idempotent — silently no-op if already earned */
    private void grant(UUID userId, String code) {
        Achievement a = achievementRepository.findByCode(code).orElse(null);
        if (a == null) return;
        if (userAchievementRepository.existsByUserIdAndAchievementId(userId, a.getId())) return;
        userAchievementRepository.save(UserAchievement.builder()
                .userId(userId)
                .achievementId(a.getId())
                .build());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> listForUser(UUID userId) {
        List<UserAchievement> earned = userAchievementRepository.findByUserId(userId);
        Map<Long, java.time.Instant> earnedAt = earned.stream()
                .collect(Collectors.toMap(UserAchievement::getAchievementId, UserAchievement::getEarnedAt));

        return achievementRepository.findAll().stream()
                .map(a -> new AchievementResponse(
                        a.getId(),
                        a.getCode(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getIcon(),
                        a.getThreshold(),
                        earnedAt.containsKey(a.getId()),
                        earnedAt.get(a.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public StreakResponse streakFor(UUID userId) {
        Streak s = streakRepository.findById(userId).orElseGet(() ->
                Streak.builder().userId(userId).currentDays(0).longestDays(0).build());
        return new StreakResponse(s.getCurrentDays(), s.getLongestDays(), s.getLastGoalDate());
    }
}
