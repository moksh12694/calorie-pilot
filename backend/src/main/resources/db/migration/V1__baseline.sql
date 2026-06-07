-- Placeholder baseline migration. The full schema is introduced in Module 2 (V2__schema.sql).
CREATE TABLE IF NOT EXISTS _baseline (
    id          SERIAL PRIMARY KEY,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
