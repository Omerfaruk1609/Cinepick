-- Movie Embedding Tablosu (pgvector entegrasyonu)
CREATE EXTENSION IF NOT EXISTS vector;
ALTER TABLE movies ADD COLUMN IF NOT EXISTS embedding vector(1536);

-- Kullanıcı Etkileşimleri (Watchlist / Favorites / Ratings)
CREATE TABLE user_movie_interactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    is_favorite BOOLEAN DEFAULT FALSE,
    in_watchlist BOOLEAN DEFAULT FALSE,
    rating DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_movie UNIQUE (user_id, movie_id)
);

-- Vektör Aramaları için HNSW index
CREATE INDEX IF NOT EXISTS idx_movies_embedding_hnsw 
ON movies USING hnsw (embedding vector_cosine_ops);
