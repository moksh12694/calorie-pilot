package com.caloriepilot.api.modules.food;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meal_entries")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "food_id", nullable = false)
    private Long foodId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private MealType meal;

    @Column(nullable = false) @Builder.Default
    private BigDecimal servings = BigDecimal.ONE;

    @Column(nullable = false) private BigDecimal calories;
    @Column(name = "protein_g", nullable = false) private BigDecimal proteinG;
    @Column(name = "carbs_g",   nullable = false) private BigDecimal carbsG;
    @Column(name = "fat_g",     nullable = false) private BigDecimal fatG;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
