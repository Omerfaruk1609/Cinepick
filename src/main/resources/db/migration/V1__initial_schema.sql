-- pgvector eklentisinin aktif edilmesi
CREATE EXTENSION IF NOT EXISTS vector;

-- Kullanıcılar Tablosu
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    has_completed_onboarding BOOLEAN NOT NULL DEFAULT FALSE,
    user_vector vector(1536),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Filmler Tablosu
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    tmdb_id BIGINT UNIQUE,
    title VARCHAR(255) NOT NULL,
    overview TEXT,
    poster_path VARCHAR(255),
    release_date DATE,
    vote_average DOUBLE PRECISION,
    genres VARCHAR(255)[]
);
