package com.caloriepilot.api.modules.steps;

import com.caloriepilot.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "step_logs", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "log_date"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StepLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private Integer steps;

    @Column(name = "distance_m")
    private BigDecimal distanceM;

    @Column
    private BigDecimal calories;

    @Column(nullable = false, length = 32)
    @Builder.Default
    private String source = "pedometer";
}
