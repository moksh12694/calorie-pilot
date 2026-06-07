package com.caloriepilot.api.modules.achievements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAchievementRepository
        extends JpaRepository<UserAchievement, UserAchievement.UserAchievementId> {

    List<UserAchievement> findByUserId(UUID userId);

    boolean existsByUserIdAndAchievementId(UUID userId, Long achievementId);
}
