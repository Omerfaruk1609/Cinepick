ALTER TABLE movies ADD COLUMN IF NOT EXISTS streaming_platforms VARCHAR(500);

CREATE INDEX IF NOT EXISTS idx_movies_streaming_platforms ON movies (streaming_platforms);

CREATE INDEX IF NOT EXISTS idx_movies_embedding_hnsw ON movies USING hnsw (embedding vector_cosine_ops) WITH (m = 16, ef_construction = 64);
