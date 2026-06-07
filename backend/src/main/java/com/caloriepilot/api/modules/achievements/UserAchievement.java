package com.caloriepilot.api.modules.achievements;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_achievements")
@IdClass(UserAchievement.UserAchievementId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserAchievement {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Id
    @Column(name = "achievement_id")
    private Long achievementId;

    @Column(name = "earned_at", nullable = false)
    @Builder.Default
    private Instant earnedAt = Instant.now();

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class UserAchievementId implements Serializable {
        private UUID userId;
        private Long achievementId;

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserAchievementId that)) return false;
            return Objects.equals(userId, that.userId) && Objects.equals(achievementId, that.achievementId);
        }
        @Override public int hashCode() { return Objects.hash(userId, achievementId); }
    }
}
