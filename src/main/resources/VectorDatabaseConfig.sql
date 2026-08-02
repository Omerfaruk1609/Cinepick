-- Enable vector extension on PostgreSQL
CREATE EXTENSION IF NOT EXISTS vector;

-- Create table for storing movie overview vector embeddings
CREATE TABLE IF NOT EXISTS movie_embeddings (
    id BIGSERIAL PRIMARY KEY,
    movie_tmdb_id BIGINT NOT NULL UNIQUE REFERENCES movies(tmdb_id) ON DELETE CASCADE,
    overview_text TEXT NOT NULL,
    embedding vector(1536),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Cosine distance index for fast top-K vector similarity search
CREATE INDEX IF NOT EXISTS idx_movie_embeddings_vector 
ON movie_embeddings 
USING ivfflat (embedding vector_cosine_ops)
WITH (lists = 100);
