package proje.cinepick.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.dto.SearchResultDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.repository.MovieSearchRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hybrid movie search:
 *  - KEYWORD mode  → PostgreSQL ILIKE (title + overview)
 *  - SEMANTIC mode → pgvector cosine similarity (overview embeddings)
 *  - HYBRID  mode  → keyword ∪ semantic, dedup + score merge
 */
@Slf4j
@Service
public class MovieSearchService {

    private final MovieRepository movieRepository;
    private final MovieSearchRepository movieSearchRepository;
    private final EmbeddingModel embeddingModel;
    private final Timer searchLatencyTimer;

    public MovieSearchService(MovieRepository movieRepository,
                              MovieSearchRepository movieSearchRepository,
                              @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel,
                              MeterRegistry meterRegistry) {
        this.movieRepository = movieRepository;
        this.movieSearchRepository = movieSearchRepository;
        this.embeddingModel = embeddingModel;
        this.searchLatencyTimer = Timer.builder("cinepick.search.latency")
                .description("Movie search end-to-end latency")
                .tag("service", "MovieSearchService")
                .register(meterRegistry);
    }

    public SearchResultDto search(String query, String mode, int page, int size) {
        return searchLatencyTimer.record(() -> doSearch(query, mode, page, size));
    }

    private SearchResultDto doSearch(String query, String mode, int page, int size) {
        if (query == null || query.isBlank()) {
            return SearchResultDto.empty(query);
        }

        String normalizedQuery = query.trim();
        String resolvedMode = (mode == null || mode.isBlank()) ? "hybrid" : mode.toLowerCase();

        List<Movie> results = switch (resolvedMode) {
            case "keyword"  -> keywordSearch(normalizedQuery, page, size);
            case "semantic" -> semanticSearch(normalizedQuery, size);
            case "hybrid"   -> hybridSearch(normalizedQuery, page, size);
            default         -> hybridSearch(normalizedQuery, page, size);
        };

        List<MovieDto> dtos = results.stream()
                .map(MovieDto::fromEntity)
                .collect(Collectors.toList());

        return SearchResultDto.builder()
                .query(normalizedQuery)
                .mode(resolvedMode)
                .results(dtos)
                .totalResults(dtos.size())
                .page(page)
                .size(size)
                .build();
    }

    // ─── Keyword Search ───────────────────────────────────────────────────────

    List<Movie> keywordSearch(String query, int page, int size) {
        int offset = page * size;
        return movieSearchRepository.findByKeyword(query, size, offset);
    }

    // ─── Semantic / Vector Search ─────────────────────────────────────────────

    List<Movie> semanticSearch(String query, int limit) {
        try {
            long start = System.nanoTime();
            List<Double> queryEmbedding = embeddingModel.embed(query);
            long embeddingMs = (System.nanoTime() - start) / 1_000_000;
            log.debug("Embedding generated in {} ms for query: '{}'", embeddingMs, query);

            String vectorString = toVectorString(queryEmbedding);

            long searchStart = System.nanoTime();
            List<Long> tmdbIds = movieSearchRepository.findTopKBySemanticSimilarity(vectorString, limit);
            long searchMs = (System.nanoTime() - searchStart) / 1_000_000;
            log.debug("Vector search completed in {} ms, found {} results", searchMs, tmdbIds.size());

            return tmdbIds.stream()
                    .map(tmdbId -> movieRepository.findByTmdbId(tmdbId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Semantic search failed, falling back to keyword: {}", e.getMessage());
            return keywordSearch(query, 0, limit);
        }
    }

    // ─── Hybrid Search (Keyword ∪ Semantic, dedup) ────────────────────────────

    List<Movie> hybridSearch(String query, int page, int size) {
        List<Movie> keywordResults = keywordSearch(query, page, size);
        List<Movie> semanticResults = semanticSearch(query, size);

        // Merge: keyword first (exact matches preferred), then semantic additions
        Map<Long, Movie> merged = new LinkedHashMap<>();
        for (Movie m : keywordResults) {
            if (m.getId() != null) merged.put(m.getId(), m);
        }
        for (Movie m : semanticResults) {
            if (m.getId() != null) merged.putIfAbsent(m.getId(), m);
        }

        return merged.values().stream()
                .limit(size)
                .collect(Collectors.toList());
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    private String toVectorString(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            sb.append(vector.get(i));
            if (i < vector.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
