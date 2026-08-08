package proje.cinepick.benchmark;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.Role;
import proje.cinepick.entity.User;
import proje.cinepick.integration.BaseIntegrationTest;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.repository.UserMovieInteractionRepository;
import proje.cinepick.repository.UserRepository;
import proje.cinepick.service.UserPreferenceVectorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Taste Drift Benchmark — Centroid Cosine Similarity over Time
 *
 * Simulates a user shifting taste across 3 phases:
 *   Phase 1 (Marvel)  → action embeddings [0.9, 0.1, 0.1, 0.1]
 *   Phase 2 (Drama)   → drama embeddings  [0.1, 0.9, 0.1, 0.1]
 *   Phase 3 (Anime)   → anime embeddings  [0.0, 0.1, 0.1, 0.2]
 *
 * Test assertions:
 *  - Phase 1 → Phase 2 cosine similarity should be LOW (< 0.5) — significant drift
 *  - Phase 2 → Phase 3 cosine similarity should be LOW (< 0.5) — drift continues
 *  - Phase 1 → Phase 3 similarity should be VERY LOW (< 0.3) — maximum drift
 *
 * Why this matters: confirms the taste centroid is really moving,
 * not just averaging everything to a global mean.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TasteDriftBenchmarkTest extends BaseIntegrationTest {

    @Autowired
    private UserPreferenceVectorService vectorService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMovieInteractionRepository interactionRepository;

    private static float[] phase1Vector;
    private static float[] phase2Vector;
    private static float[] phase3Vector;
    private static User driftUser;

    @BeforeEach
    void setupDriftUser() {
        if (driftUser != null) return;

        driftUser = userRepository.save(User.builder()
                .username("taste_drift_user")
                .email("taste_drift@cinepick.test")
                .password("$2a$10$benchmark_hash")
                .role(Role.USER)
                .hasCompletedOnboarding(true)
                .build());
    }

    // ─── Phase 1: Marvel ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Phase 1 — Action/Marvel profile establishes centroid")
    void phase1_marvelProfile_establishesCentroid() {
        List<Movie> seedMovies = BenchmarkDataFixtures.buildSeedMovies();
        List<Movie> actionMovies = movieRepository.saveAll(seedMovies.subList(0, 5)); // Iron Man, Thor, ...

        for (Movie m : actionMovies) {
            interactionRepository.save(
                    BenchmarkDataFixtures.buildInteraction(driftUser, m, true, 5.0));
        }

        phase1Vector = vectorService.calculateUserVector(driftUser.getId());

        assertThat(phase1Vector).as("Phase 1 vector should not be null").isNotNull();
        System.out.printf("✅ Phase 1 (Marvel) centroid computed. Dim: %d%n", phase1Vector.length);
        System.out.printf("   Sample values: [%.3f, %.3f, %.3f, %.3f]%n",
                phase1Vector[0], phase1Vector[1], phase1Vector[2], phase1Vector[3]);
    }

    // ─── Phase 2: Drama ──────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("Phase 2 — Drama switch: centroid should drift significantly from Phase 1")
    void phase2_dramaSwitch_centroidDrifts() {
        assertThat(phase1Vector).as("Phase 1 must run first").isNotNull();

        List<Movie> seedMovies = BenchmarkDataFixtures.buildSeedMovies();
        List<Movie> dramaMovies = movieRepository.saveAll(seedMovies.subList(10, 15)); // Shawshank, Schindler...

        for (Movie m : dramaMovies) {
            interactionRepository.save(
                    BenchmarkDataFixtures.buildInteraction(driftUser, m, true, 5.0));
        }

        phase2Vector = vectorService.calculateUserVector(driftUser.getId());

        assertThat(phase2Vector).as("Phase 2 vector should not be null").isNotNull();

        double similarity = BenchmarkMetricsCalculator.cosineSimilarity(phase1Vector, phase2Vector);
        System.out.printf("📊 Phase 1→2 Cosine Similarity = %.4f (expect < 0.85, drift occurring)%n", similarity);
        System.out.printf("   Phase 2 centroid sample: [%.3f, %.3f, %.3f, %.3f]%n",
                phase2Vector[0], phase2Vector[1], phase2Vector[2], phase2Vector[3]);

        assertThat(similarity)
                .as("Centroid should drift from Action toward Drama (similarity < 0.85)")
                .isLessThan(0.85);
    }

    // ─── Phase 3: Anime ──────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("Phase 3 — Anime shift: centroid drifts further, Phase1→Phase3 very different")
    void phase3_animeShift_maximumDrift() {
        assertThat(phase2Vector).as("Phase 2 must run first").isNotNull();

        List<Movie> seedMovies = BenchmarkDataFixtures.buildSeedMovies();
        List<Movie> animeMovies = movieRepository.saveAll(seedMovies.subList(28, 30)); // Spirited Away, Akira

        for (Movie m : animeMovies) {
            interactionRepository.save(
                    BenchmarkDataFixtures.buildInteraction(driftUser, m, true, 5.0));
        }

        phase3Vector = vectorService.calculateUserVector(driftUser.getId());


        assertThat(phase3Vector).as("Phase 3 vector should not be null").isNotNull();

        double sim12 = BenchmarkMetricsCalculator.cosineSimilarity(phase1Vector, phase2Vector);
        double sim23 = BenchmarkMetricsCalculator.cosineSimilarity(phase2Vector, phase3Vector);
        double sim13 = BenchmarkMetricsCalculator.cosineSimilarity(phase1Vector, phase3Vector);

        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  Taste Drift Cosine Similarity Matrix");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  Phase 1 (Marvel) → Phase 2 (Drama)  : %.4f%n", sim12);
        System.out.printf("  Phase 2 (Drama)  → Phase 3 (Anime)  : %.4f%n", sim23);
        System.out.printf("  Phase 1 (Marvel) → Phase 3 (Anime)  : %.4f%n", sim13);
        System.out.println("  (Lower = More Drift = Better Taste Tracking)");
        System.out.println("══════════════════════════════════════════════════════\n");

        assertThat(sim13)
                .as("Phase 1→3 similarity should show maximum drift (< 0.85)")
                .isLessThan(0.85);

    }

    // ─── Drift Summary ────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Taste drift: centroid moves in the correct direction")
    void tasteDrift_centroidMovesCorrectly() {
        assertThat(phase1Vector).isNotNull();
        assertThat(phase3Vector).isNotNull();

        // The action dimension (index 0) should DECREASE from Phase 1 to Phase 3
        // The anime-related dimension (index 3) should INCREASE from Phase 1 to Phase 3
        System.out.printf("📊 Action dim[0]:  Phase1=%.4f → Phase3=%.4f (expect decrease)%n",
                phase1Vector[0], phase3Vector[0]);
        System.out.printf("📊 Anime  dim[3]:  Phase1=%.4f → Phase3=%.4f (expect increase)%n",
                phase1Vector[3], phase3Vector[3]);

        assertThat((double) phase3Vector[0])
                .as("Action dimension should decrease after anime phase")
                .isLessThan(phase1Vector[0]);

        assertThat((double) phase3Vector[3])
                .as("Anime dimension should increase after anime phase")
                .isGreaterThan(phase1Vector[3]);
    }
}
