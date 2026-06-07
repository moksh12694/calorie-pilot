package com.caloriepilot.api.modules.food;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "foods")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 120)
    private String brand;

    @Column(name = "serving_size_g", nullable = false)
    @Builder.Default
    private BigDecimal servingSizeG = new BigDecimal("100");

    @Column(nullable = false)
    private BigDecimal calories;

    @Column(name = "protein_g", nullable = false) @Builder.Default private BigDecimal proteinG = BigDecimal.ZERO;
    @Column(name = "carbs_g",   nullable = false) @Builder.Default private BigDecimal carbsG   = BigDecimal.ZERO;
    @Column(name = "fat_g",     nullable = false) @Builder.Default private BigDecimal fatG     = BigDecimal.ZERO;
    @Column(name = "fiber_g",   nullable = false) @Builder.Default private BigDecimal fiberG   = BigDecimal.ZERO;

    @Column(name = "is_public", nullable = false) @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
