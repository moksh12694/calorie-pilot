-- =========================================================
-- CaloriePilot — full schema
-- =========================================================

-- Extensions (must come before any index that uses them)
CREATE EXTENSION IF NOT EXISTS pgcrypto;   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pg_trgm;    -- trigram food search

-- ---------- USERS ----------
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(120) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(20),
    height_cm       NUMERIC(5,2),
    timezone        VARCHAR(64)  NOT NULL DEFAULT 'UTC',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_email ON users(email);

-- ---------- USER GOALS ----------
CREATE TABLE user_goals (
    user_id            UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    daily_step_goal    INTEGER NOT NULL DEFAULT 10000 CHECK (daily_step_goal > 0),
    daily_calorie_goal INTEGER NOT NULL DEFAULT 2000  CHECK (daily_calorie_goal > 0),
    daily_water_ml     INTEGER NOT NULL DEFAULT 2500  CHECK (daily_water_ml > 0),
    daily_protein_g    INTEGER NOT NULL DEFAULT 120,
    daily_carbs_g      INTEGER NOT NULL DEFAULT 250,
    daily_fat_g        INTEGER NOT NULL DEFAULT 70,
    target_weight_kg   NUMERIC(5,2),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---------- STEP LOGS ----------
CREATE TABLE step_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    log_date    DATE NOT NULL,
    steps       INTEGER NOT NULL CHECK (steps >= 0),
    distance_m  NUMERIC(8,2),
    calories    NUMERIC(8,2),
    source      VARCHAR(32) NOT NULL DEFAULT 'pedometer',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, log_date)
);
CREATE INDEX idx_step_logs_user_date ON step_logs(user_id, log_date DESC);

-- ---------- FOODS ----------
CREATE TABLE foods (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(160) NOT NULL,
    brand           VARCHAR(120),
    serving_size_g  NUMERIC(8,2) NOT NULL DEFAULT 100,
    calories        NUMERIC(8,2) NOT NULL CHECK (calories >= 0),
    protein_g       NUMERIC(8,2) NOT NULL DEFAULT 0,
    carbs_g         NUMERIC(8,2) NOT NULL DEFAULT 0,
    fat_g           NUMERIC(8,2) NOT NULL DEFAULT 0,
    fiber_g         NUMERIC(8,2) NOT NULL DEFAULT 0,
    is_public       BOOLEAN NOT NULL DEFAULT TRUE,
    created_by      UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_foods_name_trgm ON foods USING GIN (LOWER(name) gin_trgm_ops);
CREATE INDEX idx_foods_brand     ON foods (LOWER(brand));

-- ---------- MEAL ENTRIES ----------
CREATE TABLE meal_entries (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    food_id     BIGINT NOT NULL REFERENCES foods(id),
    log_date    DATE NOT NULL,
    meal        VARCHAR(16) NOT NULL CHECK (meal IN ('BREAKFAST','LUNCH','DINNER','SNACK')),
    servings    NUMERIC(6,2) NOT NULL DEFAULT 1 CHECK (servings > 0),
    calories    NUMERIC(8,2) NOT NULL,
    protein_g   NUMERIC(8,2) NOT NULL,
    carbs_g     NUMERIC(8,2) NOT NULL,
    fat_g       NUMERIC(8,2) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_meal_user_date ON meal_entries(user_id, log_date DESC);

-- ---------- WATER LOGS ----------
CREATE TABLE water_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    log_date    DATE NOT NULL,
    amount_ml   INTEGER NOT NULL CHECK (amount_ml > 0),
    logged_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_water_user_date ON water_logs(user_id, log_date DESC);

-- ---------- WEIGHT LOGS ----------
CREATE TABLE weight_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    log_date    DATE NOT NULL,
    weight_kg   NUMERIC(5,2) NOT NULL CHECK (weight_kg > 0),
    note        VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, log_date)
);
CREATE INDEX idx_weight_user_date ON weight_logs(user_id, log_date DESC);

-- ---------- PUSH DEVICES ----------
CREATE TABLE push_devices (
    id              BIGSERIAL PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expo_push_token VARCHAR(255) NOT NULL,
    platform        VARCHAR(20)  NOT NULL,
    last_seen_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, expo_push_token)
);

-- ---------- NOTIFICATION LOGS (dedup) ----------
CREATE TABLE notification_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    log_date    DATE NOT NULL,
    kind        VARCHAR(24) NOT NULL CHECK (kind IN ('STEPS_80','STEPS_90','STEPS_95','STEPS_100','STEPS_EXCEEDED')),
    sent_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, log_date, kind)
);
CREATE INDEX idx_notif_user_date ON notification_logs(user_id, log_date);

-- ---------- ACHIEVEMENTS ----------
CREATE TABLE achievements (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(64) NOT NULL UNIQUE,
    title       VARCHAR(120) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon        VARCHAR(64),
    threshold   INTEGER
);

CREATE TABLE user_achievements (
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id BIGINT NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
    earned_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, achievement_id)
);

-- ---------- STREAKS ----------
CREATE TABLE streaks (
    user_id          UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    current_days     INTEGER NOT NULL DEFAULT 0,
    longest_days     INTEGER NOT NULL DEFAULT 0,
    last_goal_date   DATE,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
