package com.caloriepilot.api.modules.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_goals")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserGoal {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "daily_step_goal",    nullable = false) @Builder.Default private Integer dailyStepGoal    = 10_000;
    @Column(name = "daily_calorie_goal", nullable = false) @Builder.Default private Integer dailyCalorieGoal = 2_000;
    @Column(name = "daily_water_ml",     nullable = false) @Builder.Default private Integer dailyWaterMl     = 2_500;
    @Column(name = "daily_protein_g",    nullable = false) @Builder.Default private Integer dailyProteinG    = 120;
    @Column(name = "daily_carbs_g",      nullable = false) @Builder.Default private Integer dailyCarbsG      = 250;
    @Column(name = "daily_fat_g",        nullable = false) @Builder.Default private Integer dailyFatG        = 70;

    @Column(name = "target_weight_kg")
    private BigDecimal targetWeightKg;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
