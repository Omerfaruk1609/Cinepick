package proje.cinepick;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import proje.cinepick.dto.IntentDiscoveryRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.dto.MovieFilterRequest;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.service.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinalUserVerificationTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CachedUserVectorService userVectorService;

    @Mock
    private MatchCalculatorService matchCalculatorService;

    @Mock
    private AsyncExplanationService asyncExplanationService;

    @Mock
    private BlacklistService blacklistService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private LocalEmbeddingService localEmbeddingService;

    private MeterRegistry meterRegistry;

    private IntentDiscoveryService intentDiscoveryService;

    private PersonalizedRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        intentDiscoveryService = new IntentDiscoveryService(localEmbeddingService, movieRepository);

        recommendationService = new PersonalizedRecommendationService(
                movieRepository,
                userVectorService,
                matchCalculatorService,
                asyncExplanationService,
                blacklistService,
                redisTemplate,
                meterRegistry
        );
    }

    @Test
    void endToEnd_MovieFilteringWith50Limit_ReturnsFilteredDtos() {
        Movie m1 = Movie.builder().id(1L).tmdbId(10L).title("The Matrix").voteAverage(8.7).releaseYear(1999).build();
        Movie m2 = Movie.builder().id(2L).tmdbId(20L).title("Inception").voteAverage(8.8).releaseYear(2010).build();

        when(movieRepository.filterMovies(any(), any(), any(), any(), any(), any(), any(), any(), eq(50), eq(0)))
                .thenReturn(List.of(m1, m2));

        MovieFilterRequest request = MovieFilterRequest.builder()
                .genres(List.of("Bilim Kurgu"))
                .originalLanguage("en")
                .minYear(1990)
                .maxYear(2020)
                .limit(50)
                .page(0)
                .build();

        List<MovieDto> results = recommendationService.filterMovies(null, request);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTitle()).isEqualTo("The Matrix");
        assertThat(results.get(1).getTitle()).isEqualTo("Inception");
    }

    @Test
    void endToEnd_IntentDiscovery_GeneratesEmbeddingAndReturnsMatches() {
        Movie m1 = Movie.builder().id(100L).tmdbId(500L).title("Pulp Fiction").voteAverage(8.9).releaseYear(1994).build();
        when(movieRepository.filterMovies(any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(m1));

        IntentDiscoveryRequest request = IntentDiscoveryRequest.builder()
                .prompt("90'lar nostalji filmi")
                .limit(10)
                .build();

        List<MovieDto> discoveryResults = intentDiscoveryService.discoverByIntent(request);

        assertThat(discoveryResults).hasSize(1);
        assertThat(discoveryResults.get(0).getTitle()).isEqualTo("Pulp Fiction");
    }
}
