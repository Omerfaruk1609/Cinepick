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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Latency Benchmark Test
 *
 * Measures:
 *  - Recommendation service end-to-end latency (target: < 200ms)
 *  - Redis get latency (target: < 5ms)
 *  - Redis set latency (target: < 5ms)
 *  - PostgreSQL query latency via JDBC (target: < 50ms)
 *
 * Warm-up runs are excluded from the measured results.
 * Each assertion shows the p50 (median) of N_ITERATIONS runs.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LatencyBenchmarkTest extends BaseIntegrationTest {

    private static final int N_ITERATIONS = 20;
    private static final int WARMUP = 3;

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

    private static User latencyUser;

    @BeforeEach
    void setupUser() {
        if (latencyUser != null) return;

        latencyUser = userRepository.save(User.builder()
                .username("latency_bench_user")
                .email("latency@cinepick.test")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .build());

        List<Movie> movies = movieRepository.saveAll(
                BenchmarkDataFixtures.buildSeedMovies().subList(0, 5));
        movies.forEach(m -> interactionRepository.save(
                BenchmarkDataFixtures.buildInteraction(latencyUser, m, true, 5.0)));
    }

    // ─── Recommendation API Latency ───────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Recommendation API p50 latency < 200ms")
    void recommendationLatency_p50_shouldBeLessThan200ms() {
        // Warm-up
        for (int i = 0; i < WARMUP; i++) {
            recommendationService.getPersonalizedRecommendations(latencyUser.getId(), List.of("Action"), 10);
        }

        long[] latencies = new long[N_ITERATIONS];
        for (int i = 0; i < N_ITERATIONS; i++) {
            // Evict cache to measure cold recommendation (no Redis hit)
            redisTemplate.delete("user:rec:" + latencyUser.getId());

            long start = System.nanoTime();
            recommendationService.getPersonalizedRecommendations(latencyUser.getId(), List.of("Action"), 10);
            latencies[i] = (System.nanoTime() - start) / 1_000_000; // ms
        }

        long p50 = percentile(latencies, 50);
        long p95 = percentile(latencies, 95);
        long p99 = percentile(latencies, 99);
        long avg = average(latencies);

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("  Recommendation API Latency (" + N_ITERATIONS + " cold runs)");
        System.out.println("══════════════════════════════════════════════════");
        System.out.printf("  avg  : %d ms%n", avg);
        System.out.printf("  p50  : %d ms  (target: < 200ms)%n", p50);
        System.out.printf("  p95  : %d ms  (target: < 500ms)%n", p95);
        System.out.printf("  p99  : %d ms%n", p99);
        System.out.println("══════════════════════════════════════════════════\n");

        assertThat(p50).as("Recommendation API p50 < 200ms").isLessThan(200L);
        assertThat(p95).as("Recommendation API p95 < 500ms").isLessThan(500L);
    }

    // ─── Redis Cache Hit Latency ──────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("Redis GET latency < 5ms (cached recommendation)")
    void redisGetLatency_shouldBeLessThan5ms() {
        // Pre-warm: put something in cache
        String key = "bench:latency:test";
        redisTemplate.opsForValue().set(key, "benchmark_value");

        // Warm-up
        for (int i = 0; i < WARMUP; i++) {
            redisTemplate.opsForValue().get(key);
        }

        long[] latencies = new long[N_ITERATIONS];
        for (int i = 0; i < N_ITERATIONS; i++) {
            long start = System.nanoTime();
            redisTemplate.opsForValue().get(key);
            latencies[i] = (System.nanoTime() - start) / 1_000_000;
        }

        long p50 = percentile(latencies, 50);
        long p95 = percentile(latencies, 95);

        System.out.printf("📊 Redis GET — p50: %d ms, p95: %d ms (target: < 5ms)%n", p50, p95);

        assertThat(p50).as("Redis GET p50 should be < 5ms").isLessThan(5L);
    }

    // ─── Redis Cache Set Latency ──────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("Redis SET latency < 5ms")
    void redisSetLatency_shouldBeLessThan5ms() {
        long[] latencies = new long[N_ITERATIONS];
        for (int i = 0; i < N_ITERATIONS; i++) {
            long start = System.nanoTime();
            redisTemplate.opsForValue().set("bench:set:" + i, "value_" + i);
            latencies[i] = (System.nanoTime() - start) / 1_000_000;
        }

        long p50 = percentile(latencies, 50);
        System.out.printf("📊 Redis SET — p50: %d ms (target: < 5ms)%n", p50);

        assertThat(p50).as("Redis SET p50 should be < 5ms").isLessThan(5L);
    }

    // ─── Cache Hit vs Miss Latency Comparison ────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Cache hit should be significantly faster than cache miss")
    void cacheHit_shouldBeFasterThanCacheMiss() {
        // Clear cache
        redisTemplate.delete("user:rec:" + latencyUser.getId());

        // Cache MISS: measure cold path
        long missStart = System.nanoTime();
        recommendationService.getPersonalizedRecommendations(latencyUser.getId(), List.of("Action"), 10);
        long missLatency = (System.nanoTime() - missStart) / 1_000_000;

        // Cache HIT: now it's cached
        long hitStart = System.nanoTime();
        recommendationService.getPersonalizedRecommendations(latencyUser.getId(), List.of("Action"), 10);
        long hitLatency = (System.nanoTime() - hitStart) / 1_000_000;

        System.out.printf("📊 Cache Miss: %d ms | Cache Hit: %d ms | Speedup: %.1fx%n",
                missLatency, hitLatency, (double) missLatency / Math.max(1, hitLatency));

        assertThat(hitLatency)
                .as("Cache hit should be faster than cache miss")
                .isLessThan(missLatency);
    }

    // ─── DB Query Latency ────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("PostgreSQL findTop10 query < 50ms")
    void dbQueryLatency_shouldBeLessThan50ms() {
        // Warm-up
        for (int i = 0; i < WARMUP; i++) {
            movieRepository.findTop10ByOrderByVoteAverageDesc();
        }

        long[] latencies = new long[N_ITERATIONS];
        for (int i = 0; i < N_ITERATIONS; i++) {
            long start = System.nanoTime();
            movieRepository.findTop10ByOrderByVoteAverageDesc();
            latencies[i] = (System.nanoTime() - start) / 1_000_000;
        }

        long p50 = percentile(latencies, 50);
        long p95 = percentile(latencies, 95);

        System.out.printf("📊 DB Query — p50: %d ms, p95: %d ms (target: < 50ms)%n", p50, p95);

        assertThat(p50).as("DB query p50 should be < 50ms").isLessThan(50L);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private long percentile(long[] sortedValues, int percentile) {
        long[] sorted = sortedValues.clone();
        java.util.Arrays.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0 * sorted.length) - 1;
        return sorted[Math.max(0, index)];
    }

    private long average(long[] values) {
        long sum = 0;
        for (long v : values) sum += v;
        return sum / values.length;
    }
}
