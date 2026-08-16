ALTER TABLE movies ADD COLUMN IF NOT EXISTS original_language VARCHAR(10);
ALTER TABLE movies ADD COLUMN IF NOT EXISTS runtime INTEGER;
ALTER TABLE movies ADD COLUMN IF NOT EXISTS country VARCHAR(100);
ALTER TABLE movies ADD COLUMN IF NOT EXISTS release_year INTEGER;

CREATE INDEX IF NOT EXISTS idx_movies_original_language ON movies (original_language);
CREATE INDEX IF NOT EXISTS idx_movies_runtime ON movies (runtime);
CREATE INDEX IF NOT EXISTS idx_movies_country ON movies (country);
CREATE INDEX IF NOT EXISTS idx_movies_release_year ON movies (release_year);
