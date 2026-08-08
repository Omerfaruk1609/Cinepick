package proje.cinepick.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.UserBlacklist;
import proje.cinepick.repository.MovieRepository;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class PersonalizedRecommendationService {

    private final MovieRepository movieRepository;
    private final CachedUserVectorService userVectorService;
    private final MatchCalculatorService matchCalculatorService;
    private final AsyncExplanationService asyncExplanationService;
    private final BlacklistService blacklistService;
    private final RedisTemplate<String, Object> redisTemplate;

    // ─── Micrometer Metrics ───────────────────────────────────────────────────
    private final Timer recommendationTimer;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public PersonalizedRecommendationService(
            MovieRepository movieRepository,
            CachedUserVectorService userVectorService,
            MatchCalculatorService matchCalculatorService,
            AsyncExplanationService asyncExplanationService,
            BlacklistService blacklistService,
            RedisTemplate<String, Object> redisTemplate,
            MeterRegistry meterRegistry) {
        this.movieRepository = movieRepository;
        this.userVectorService = userVectorService;
        this.matchCalculatorService = matchCalculatorService;
        this.asyncExplanationService = asyncExplanationService;
        this.blacklistService = blacklistService;
        this.redisTemplate = redisTemplate;

        this.recommendationTimer = Timer.builder("cinepick.recommendation.latency")
                .description("Personalized recommendation generation latency")
                .tag("service", "PersonalizedRecommendationService")
                .register(meterRegistry);
        this.cacheHitCounter = Counter.builder("cinepick.recommendation.cache.hit")
                .description("Number of recommendation cache hits")
                .register(meterRegistry);
        this.cacheMissCounter = Counter.builder("cinepick.recommendation.cache.miss")
                .description("Number of recommendation cache misses")
                .register(meterRegistry);
    }

    public List<MovieDto> getPersonalizedRecommendations(Long userId, List<String> favoriteGenres, int limit) {
        return recommendationTimer.record(() -> generateRecommendations(userId, favoriteGenres, limit));
    }

    private List<MovieDto> generateRecommendations(Long userId, List<String> favoriteGenres, int limit) {
        String cacheKey = "user:rec:" + userId;

        // 1. Önbellek Kontrolü (Redis)
        try {
            @SuppressWarnings("unchecked")
            List<MovieDto> cachedRecs = (List<MovieDto>) redisTemplate.opsForValue().get(cacheKey);
            if (cachedRecs != null) {
                cacheHitCounter.increment();
                log.info("Personalized recommendations fetched from Redis cache for userId: {}", userId);
                return cachedRecs;
            }
            cacheMissCounter.increment();
        } catch (Exception e) {
            log.warn("Redis read failed for recommendation cache: {}", e.getMessage());
            cacheMissCounter.increment();
        }

        // 2. Kullanıcı Profil Vektörünü Getir ($V_user)
        float[] userVector = userVectorService.getUserVectorWithCache(userId);

        // Eğer kullanıcı henüz yeni ise (Soğuk Başlama) en yüksek puanlı filmleri dön
        if (userVector == null) {
            log.info("Cold start user (userId: {}), returning fallback popular movies", userId);
            return getFallbackPopularMovies(limit);
        }

        // 3. Float Array -> pgvector String Formatına Dönüştürme "[0.12, -0.45, ...]"
        String vectorString = Arrays.toString(userVector);

        // 4. Kullanıcı Kara Listesini Çek
        UserBlacklist blacklist = blacklistService.getBlacklist(userId);
        String[] excludedGenres = blacklist.getExcludedGenres();
        String[] excludedDirectors = blacklist.getExcludedDirectors();

        // 5. pgvector Hibrit Sorgusu + Kara Liste Filtreleri
        List<String> safeGenres = favoriteGenres != null ? favoriteGenres : List.of();
        String[] genreArray = safeGenres.toArray(new String[0]);

        List<Movie> recommendedMovies = movieRepository.findPersonalizedRecommendationsWithBlacklist(
                userId,
                vectorString,
                genreArray,
                excludedGenres,
                excludedDirectors,
                limit
        );

        List<MovieDto> dtoList = enrichMoviesWithMatchScore(recommendedMovies, userVector, safeGenres);

        // 5. Sonuçları 1 Saatliğine Redis'e Kaydet
        try {
            redisTemplate.opsForValue().set(cacheKey, dtoList, Duration.ofHours(1));
        } catch (Exception e) {
            log.warn("Redis write failed for recommendation cache: {}", e.getMessage());
        }

        return dtoList;
    }

    public List<MovieDto> enrichMoviesWithMatchScore(List<Movie> movies, float[] userVector, List<String> favoriteGenres) {
        return movies.stream().map(movie -> {
            double similarity = proje.cinepick.util.VectorMathUtil.cosineSimilarity(userVector, movie.getEmbedding());
            boolean genreMatch = movie.getGenres() != null && favoriteGenres.stream()
                    .anyMatch(g -> Arrays.asList(movie.getGenres()).contains(g));

            int matchPercentage = matchCalculatorService.calculateMatchPercentage(
                    similarity,
                    genreMatch,
                    movie.getVoteAverage()
            );

            MovieDto dto = MovieDto.fromEntity(movie);
            dto.setMatchPercentage(matchPercentage);

            String reason = asyncExplanationService.getOrGenerateReason(
                    1L,
                    movie.getTitle(),
                    favoriteGenres,
                    movie.getOverview()
            );
            dto.setRecommendationReason(reason);

            return dto;
        }).toList();
    }

    private List<MovieDto> getFallbackPopularMovies(int limit) {
        return movieRepository.findTop10ByOrderByVoteAverageDesc()
                .stream()
                .limit(limit)
                .map(MovieDto::fromEntity)
                .toList();
    }
}
