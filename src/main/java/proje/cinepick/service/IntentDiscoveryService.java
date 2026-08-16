package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.IntentDiscoveryRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentDiscoveryService {

    private final LocalEmbeddingService localEmbeddingService;
    private final MovieRepository movieRepository;

    public List<MovieDto> discoverByIntent(IntentDiscoveryRequest request) {
        if (request == null || request.getEffectiveQuery() == null || request.getEffectiveQuery().trim().isEmpty()) {
            throw new IllegalArgumentException("Query/Prompt cannot be empty for intent discovery");
        }

        String searchPrompt = request.getEffectiveQuery();
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 20;

        log.info("Performing intent discovery for prompt: '{}' with limit {}", searchPrompt, limit);

        float[] promptVector = localEmbeddingService.generateEmbedding(searchPrompt);
        String vectorString = Arrays.toString(promptVector);

        List<Movie> matchedMovies = movieRepository.filterMovies(
                vectorString,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                limit,
                0
        );

        return matchedMovies.stream()
                .map(MovieDto::fromEntity)
                .toList();
    }
}
