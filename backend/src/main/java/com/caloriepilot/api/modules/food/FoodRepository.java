package com.caloriepilot.api.modules.food;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("""
           SELECT f FROM Food f
            WHERE (f.isPublic = true OR f.createdBy = :userId)
              AND LOWER(f.name) LIKE LOWER(CONCAT('%', :q, '%'))
            ORDER BY LENGTH(f.name) ASC, f.name ASC
           """)
    Page<Food> search(@Param("q") String q, @Param("userId") UUID userId, Pageable pageable);
}
