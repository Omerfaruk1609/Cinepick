package proje.cinepick.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proje.cinepick.dto.TmdbMovieDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResilientTmdbService {

    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key:dummy_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api.url:https://api.themoviedb.org/3}")
    private String tmdbApiUrl;

    @Cacheable(value = "tmdb_movies", key = "#tmdbId")
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "getMovieFallback")
    @RateLimiter(name = "tmdbApi", fallbackMethod = "getMovieFallback")
    public TmdbMovieDto getMovieDetails(Long tmdbId) {
        String url = String.format("%s/movie/%d?api_key=%s", tmdbApiUrl, tmdbId, tmdbApiKey);
        log.info("Fetching movie details from TMDB API for ID: {}", tmdbId);
        return restTemplate.getForObject(url, TmdbMovieDto.class);
    }

    public TmdbMovieDto getMovieFallback(Long tmdbId, Throwable throwable) {
        log.warn("TMDB API fallback triggered for movie ID {}. Reason: {}", tmdbId, throwable.getMessage());
        return TmdbMovieDto.builder()
                .id(tmdbId)
                .title("Fallback Movie Title")
                .overview("TMDB API is currently unavailable or rate-limited. Serving cached or fallback data.")
                .posterPath("/placeholder.jpg")
                .releaseDate("2024-01-01")
                .voteAverage(7.0)
                .runtime(120)
                .genres(List.of("Drama", "Cinema"))
                .build();
    }

    @Cacheable(value = "tmdb_providers", key = "#tmdbMovieId")
    public proje.cinepick.dto.tmdb.TmdbWatchProviderResponse.CountryProviders getTurkeyWatchProviders(Long tmdbMovieId) {
        String url = String.format("%s/movie/%d/watch/providers?api_key=%s", 
                tmdbApiUrl, tmdbMovieId, tmdbApiKey);

        try {
            proje.cinepick.dto.tmdb.TmdbWatchProviderResponse response = 
                restTemplate.getForObject(url, proje.cinepick.dto.tmdb.TmdbWatchProviderResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults().get("TR");
            }
        } catch (Exception e) {
            log.error("TMDB Watch Providers çekilemedi. Movie ID: {}", tmdbMovieId, e);
        }
        return null;
    }
}
