package com.caloriepilot.api.modules.achievements;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "streaks")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Streak {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "current_days", nullable = false) @Builder.Default private Integer currentDays = 0;
    @Column(name = "longest_days", nullable = false) @Builder.Default private Integer longestDays = 0;

    @Column(name = "last_goal_date")
    private LocalDate lastGoalDate;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
