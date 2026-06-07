package com.caloriepilot.api.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {
}
