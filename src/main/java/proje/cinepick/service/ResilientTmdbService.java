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
        log.info("Fetching movie details from TMDB API for ID: {}", tmdbId);
        return executeTmdbGet(String.format("/movie/%d", tmdbId), TmdbMovieDto.class);
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

    @Cacheable(value = "tmdb_providers", key = "#tmdbMovieId", unless = "#result == null")
    public proje.cinepick.dto.tmdb.TmdbWatchProviderResponse.CountryProviders getTurkeyWatchProviders(Long tmdbMovieId) {
        try {
            proje.cinepick.dto.tmdb.TmdbWatchProviderResponse response =
                    executeTmdbGet(String.format("/movie/%d/watch/providers", tmdbMovieId), proje.cinepick.dto.tmdb.TmdbWatchProviderResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults().get("TR");
            }
        } catch (Exception e) {
            log.error("TMDB Watch Providers çekilemedi. Movie ID: {}", tmdbMovieId, e);
        }
        return null;
    }

    public <T> T executeTmdbGet(String endpointPath, Class<T> responseType) {
        if (tmdbApiKey == null || tmdbApiKey.isBlank() || tmdbApiKey.equals("dummy_key") || tmdbApiKey.equals("your_tmdb_api_key_here")) {
            return null;
        }

        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "CinePick/2.0");

            String fullUrl;
            if (tmdbApiKey.startsWith("eyJ") || tmdbApiKey.length() > 45) {
                headers.setBearerAuth(tmdbApiKey.trim());
                fullUrl = tmdbApiUrl + endpointPath;
            } else {
                String separator = endpointPath.contains("?") ? "&" : "?";
                fullUrl = String.format("%s%s%sapi_key=%s", tmdbApiUrl, endpointPath, separator, tmdbApiKey.trim());
            }

            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            org.springframework.http.ResponseEntity<T> response = restTemplate.exchange(
                    fullUrl,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    responseType
            );
            return response.getBody();
        } catch (Exception e) {
            log.warn("TMDB request failed for path '{}': {}", endpointPath, e.getMessage());
            return null;
        }
    }
}
