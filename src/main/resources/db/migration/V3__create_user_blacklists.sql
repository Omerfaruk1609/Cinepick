CREATE TABLE IF NOT EXISTS user_blacklists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    excluded_genres VARCHAR(255)[],
    excluded_directors VARCHAR(255)[]
);

ALTER TABLE movies ADD COLUMN IF NOT EXISTS director VARCHAR(255);
