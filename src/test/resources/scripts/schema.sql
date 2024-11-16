CREATE TABLE IF NOT EXISTS "leaderboard_entry" (
    "time_score" REAL,
    "created_at" TIMESTAMP WITH TIME ZONE,
    "level" BIGINT,
    "id" VARCHAR(255) PRIMARY KEY,
    "player_name" VARCHAR(255)
);