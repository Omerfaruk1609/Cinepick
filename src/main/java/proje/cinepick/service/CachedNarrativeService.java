package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedNarrativeService {

    private final SpringAiNarrativeService springAiNarrativeService;

    @Cacheable(value = "narrative_analyses", key = "#movieData.tmdbId")
    public MovieAnalysisResponse analyzeMovieWithCache(MovieAnalysisRequest movieData) {
        log.info("Cache miss for movie narrative analysis ID: {}", movieData.getTmdbId());
        return springAiNarrativeService.analyzeMovie(movieData);
    }

    @CacheEvict(value = "narrative_analyses", allEntries = true)
    public void clearNarrativeCache() {
        log.info("Evicting all cached narrative analyses.");
    }
}
