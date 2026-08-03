package proje.cinepick.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MovieEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final JdbcTemplate jdbcTemplate;

    public MovieEmbeddingService(
            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel,
            JdbcTemplate jdbcTemplate) {
        this.embeddingModel = embeddingModel;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveMovieEmbedding(Long tmdbId, String overview) {
        if (overview == null || overview.isBlank()) {
            return;
        }

        try {
            List<Double> embedding = embeddingModel.embed(overview);
            String vectorString = convertToPgVectorString(embedding);

            String sql = """
                INSERT INTO movie_embeddings (movie_tmdb_id, overview_text, embedding)
                VALUES (?, ?, ?::vector)
                ON CONFLICT (movie_tmdb_id) DO UPDATE 
                SET overview_text = EXCLUDED.overview_text, embedding = EXCLUDED.embedding;
                """;

            jdbcTemplate.update(sql, tmdbId, overview, vectorString);
        } catch (Exception e) {
            log.error("Failed to generate or save vector embedding for movie ID {}", tmdbId, e);
        }
    }

    public List<Long> findTopKSimilarMovies(String queryText, int topK) {
        if (queryText == null || queryText.isBlank()) {
            return List.of();
        }

        try {
            List<Double> queryEmbedding = embeddingModel.embed(queryText);
            String vectorString = convertToPgVectorString(queryEmbedding);

            String sql = """
                SELECT movie_tmdb_id 
                FROM movie_embeddings 
                ORDER BY embedding <=> ?::vector 
                LIMIT ?;
                """;

            return jdbcTemplate.queryForList(sql, Long.class, vectorString, topK);
        } catch (Exception e) {
            log.error("Failed to query vector similarity for query: {}", queryText, e);
            return List.of();
        }
    }

    private String convertToPgVectorString(List<?> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            sb.append(vector.get(i));
            if (i < vector.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
