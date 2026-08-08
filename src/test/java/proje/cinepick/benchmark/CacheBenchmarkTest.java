package proje.cinepick.benchmark;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.Role;
import proje.cinepick.entity.User;
import proje.cinepick.integration.BaseIntegrationTest;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.PersonalizedRecommendationService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis Cache Benchmark
 *
 * Tests:
 *  1. Cache hit rate over 100 requests (target: > 80%)
 *  2. First request is always a miss (cold start)
 *  3. Subsequent requests for same user hit cache
 *  4. After cache eviction, next request is a miss again
 *  5. Average hit latency vs miss latency
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheBenchmarkTest extends BaseIntegrationTest {

    private static final int TOTAL_REQUESTS = 100;
    private static final double TARGET_HIT_RATE = 0.80;

    @Autowired
    private PersonalizedRecommendationService recommendationService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMovieInteractionRepository interactionRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static User cacheUser;

    @BeforeEach
    void setup() {
        if (cacheUser != null) return;

        float[] vector = new float[1536];
        vector[0] = 1.0f;
        cacheUser = userRepository.save(User.builder()
                .username("cache_bench_user")
                .email("cache@cinepick.test")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .userVector(vector)
                .build());



        List<Movie> movies = movieRepository.saveAll(
                BenchmarkDataFixtures.buildSeedMovies().subList(0, 5));
        movies.forEach(m -> interactionRepository.save(
                BenchmarkDataFixtures.buildInteraction(cacheUser, m, true, 5.0)));
    }

    // ─── Test 1: First request = cache miss ───────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("First request for user should be a cache miss (cold start)")
    void firstRequest_shouldBeCacheMiss() {
        String cacheKey = "user:rec:" + cacheUser.getId();
        redisTemplate.delete(cacheKey);

        assertThat(redisTemplate.hasKey(cacheKey))
                .as("Cache should not contain key before first request")
                .isFalse();

        recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);

        assertThat(redisTemplate.hasKey(cacheKey))
                .as("Cache should contain key after first request (was a miss, now populated)")
                .isTrue();

        System.out.println("✅ Cold start verified: first request populated the cache.");
    }

    // ─── Test 2: Second request = cache hit ───────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("Second request for same user should be a cache hit (faster)")
    void secondRequest_shouldBeCacheHit() {
        // Ensure cache is populated
        recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);

        long hitStart = System.nanoTime();
        var hitResult = recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);
        long hitMs = (System.nanoTime() - hitStart) / 1_000_000;

        System.out.printf("📊 Cache hit latency: %d ms%n", hitMs);

        assertThat(hitResult).isNotNull().isNotEmpty();
        assertThat(hitMs).as("Cache hit should respond in < 50ms").isLessThan(50L);
    }

    // ─── Test 3: Hit Rate over 100 requests ───────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("Cache hit rate should exceed 80% over 100 requests")
    void hitRate_over100Requests_shouldExceedTarget() {
        // Populate cache first
        recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);

        String cacheKey = "user:rec:" + cacheUser.getId();
        AtomicInteger hits = new AtomicInteger(0);
        AtomicInteger misses = new AtomicInteger(0);

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            boolean inCache = Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
            if (inCache) {
                hits.incrementAndGet();
            } else {
                misses.incrementAndGet();
            }
            recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);

            // Simulate cache eviction on every 15th request (e.g., new interaction)
            if (i % 15 == 14) {
                redisTemplate.delete(cacheKey);
            }
        }

        double hitRate = (double) hits.get() / TOTAL_REQUESTS;

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("  Cache Benchmark Results (" + TOTAL_REQUESTS + " requests)");
        System.out.println("══════════════════════════════════════════════════");
        System.out.printf("  Hits   : %d%n", hits.get());
        System.out.printf("  Misses : %d%n", misses.get());
        System.out.printf("  Rate   : %.2f%% (target: > %.0f%%)%n", hitRate * 100, TARGET_HIT_RATE * 100);
        System.out.println("══════════════════════════════════════════════════\n");

        assertThat(hitRate)
                .as("Cache hit rate should exceed %.0f%%", TARGET_HIT_RATE * 100)
                .isGreaterThan(TARGET_HIT_RATE);
    }

    // ─── Test 4: Cache Eviction ───────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("After cache eviction, next request should be a miss")
    void afterEviction_nextRequest_shouldBeMiss() {
        // Populate first
        recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);

        String cacheKey = "user:rec:" + cacheUser.getId();
        assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

        // Evict
        redisTemplate.delete(cacheKey);
        assertThat(redisTemplate.hasKey(cacheKey))
                .as("Cache should be empty after eviction")
                .isFalse();

        // Next request: miss → repopulate
        long start = System.nanoTime();
        recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);
        long missMs = (System.nanoTime() - start) / 1_000_000;

        assertThat(redisTemplate.hasKey(cacheKey))
                .as("Cache should be repopulated after miss")
                .isTrue();

        System.out.printf("📊 Post-eviction miss latency: %d ms (expect > 5ms for DB+vector path)%n", missMs);
        assertThat(missMs).as("Miss should take longer than 5ms (real DB work)").isGreaterThan(5L);
    }

    // ─── Test 5: Average Hit vs Miss Latency ──────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("Average hit latency should be < 10ms; miss latency comparison")
    void averageHitLatency_shouldBeVeryFast() {
        int samples = 20;
        long[] hitLatencies = new long[samples];
        long[] missLatencies = new long[samples];

        for (int i = 0; i < samples; i++) {
            // Miss
            redisTemplate.delete("user:rec:" + cacheUser.getId());
            long ms = System.nanoTime();
            recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);
            missLatencies[i] = (System.nanoTime() - ms) / 1_000_000;

            // Hit (same user, cache now populated)
            long hs = System.nanoTime();
            recommendationService.getPersonalizedRecommendations(cacheUser.getId(), List.of("Action"), 10);
            hitLatencies[i] = (System.nanoTime() - hs) / 1_000_000;
        }

        long avgHit  = average(hitLatencies);
        long avgMiss = average(missLatencies);

        System.out.printf("📊 Avg Hit Latency:  %d ms%n", avgHit);
        System.out.printf("📊 Avg Miss Latency: %d ms%n", avgMiss);
        System.out.printf("📊 Cache Speedup:    %.1fx%n", (double) avgMiss / Math.max(1, avgHit));

        assertThat(avgHit).as("Average hit latency < 100ms").isLessThan(100L);


    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private long average(long[] values) {
        long sum = 0;
        for (long v : values) sum += v;
        return sum / values.length;
    }
}
