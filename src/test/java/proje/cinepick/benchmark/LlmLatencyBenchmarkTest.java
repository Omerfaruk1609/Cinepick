package proje.cinepick.benchmark;

import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LLM Latency Benchmark — Ollama (Local Only)
 *
 * ⚠️  This test is tagged with @Tag("llm-benchmark").
 *     It is EXCLUDED from CI builds.
 *     Run locally ONLY when Ollama is running: ollama serve
 *
 * Usage:
 *   mvn test -Dgroups="llm-benchmark" -Dspring.ai.ollama.base-url=http://localhost:11434
 *
 * Measures:
 *   - Cold start: first LLM call (model loading + inference)
 *   - Warm p50 / p95 / p99: subsequent calls after model is loaded
 *   - Token generation throughput
 *
 * README table entry after running:
 *   LLM Cold Start : Xms
 *   LLM Warm p50   : Xms
 *   LLM Warm p95   : Xms
 */
@Tag("llm-benchmark")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LlmLatencyBenchmarkTest {

    private static final int WARM_ITERATIONS = 10;
    private static final String TEST_PROMPT = """
            Sen bir sinema uzmanısın.
            Kullanıcı Interstellar, Inception ve The Matrix'i sevmiş.
            Tek cümleyle Arrival'ı neden önereceğini açıkla.
            """;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private static long coldStartMs = -1;
    private static final List<Long> warmLatencies = new ArrayList<>();

    // ─── Cold Start ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[LLM] Cold start latency — model loading + first inference")
    void llm_coldStart_latency() {
        ChatClient client = chatClientBuilder.build();

        long start = System.nanoTime();
        String response = client.prompt()
                .user(TEST_PROMPT)
                .call().content();
        coldStartMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("🔴 LLM Cold Start: %d ms%n", coldStartMs);
        System.out.printf("   Response preview: %s...%n",
                response != null && response.length() > 80 ? response.substring(0, 80) : response);

        assertThat(response).isNotBlank();
        // Cold start can be slow (4–10s), just assert it completes
        assertThat(coldStartMs).as("Cold start should complete in < 15 seconds").isLessThan(15_000L);
    }

    // ─── Warm Requests ────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[LLM] Warm request latency — model already loaded")
    void llm_warmRequests_latency() {
        ChatClient client = chatClientBuilder.build();

        for (int i = 0; i < WARM_ITERATIONS; i++) {
            long start = System.nanoTime();
            String response = client.prompt()
                    .user(TEST_PROMPT)
                    .call().content();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            warmLatencies.add(elapsed);

            assertThat(response).isNotBlank();
            System.out.printf("  Warm run %2d: %d ms%n", i + 1, elapsed);
        }

        long p50 = percentile(warmLatencies, 50);
        long p95 = percentile(warmLatencies, 95);
        long p99 = percentile(warmLatencies, 99);
        long avg  = (long) warmLatencies.stream().mapToLong(Long::longValue).average().orElse(0);

        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  LLM Latency Benchmark — Ollama");
        System.out.println("══════════════════════════════════════════════════════");
        System.out.printf("  Cold Start : %d ms%n", coldStartMs);
        System.out.printf("  Warm avg   : %d ms%n", avg);
        System.out.printf("  Warm p50   : %d ms  (target: < 2000ms)%n", p50);
        System.out.printf("  Warm p95   : %d ms%n", p95);
        System.out.printf("  Warm p99   : %d ms%n", p99);
        System.out.println("══════════════════════════════════════════════════════");
        System.out.println("  → Copy above numbers into README benchmark table!");
        System.out.println("══════════════════════════════════════════════════════\n");

        assertThat(p50).as("LLM warm p50 < 2000ms").isLessThan(2_000L);
    }

    // ─── Recommendation Explanation Specific ─────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[LLM] Recommendation explanation quality — response is single sentence")
    void llm_recommendationExplanation_isSingleSentence() {
        ChatClient client = chatClientBuilder.build();

        String prompt = """
                Sen sinema konusunda uzman, samimi bir film rehberisin.
                Kullanıcının en sevdiği filmler: Interstellar, Inception, Arrival
                Önerilen film: Annihilation
                Önerilen filmin özeti: A biologist signs up for a dangerous expedition into a mysterious zone.
                
                GÖREV: Kullanıcıya bu filmi NEDEN önerdiğini açıklayan TEK CÜMLELİK, heyecan verici ve kişiselleştirilmiş bir gerekçe yaz.
                KURALLAR:
                1. Yanıt kesinlikle tek bir cümle olmalı.
                2. Kullanıcıya doğrudan hitap et.
                3. Geçmişte sevdiği filmlerden en alakalı olanına atıfta bulun.
                4. Tırnak işareti veya ekstra açıklama ekleme, sadece cümleyi dön.
                """;

        long start = System.nanoTime();
        String response = client.prompt()
                .user(prompt)
                .call().content();
        long elapsed = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%n📝 Explanation: %s%n", response);
        System.out.printf("⏱️  Generation time: %d ms%n%n", elapsed);

        assertThat(response).isNotBlank();
        // Single sentence check: at most 2 sentence-ending punctuation marks
        long sentenceCount = response.chars()
                .filter(c -> c == '.' || c == '!' || c == '?')
                .count();
        assertThat(sentenceCount)
                .as("Response should be a single sentence (max 2 punctuation marks)")
                .isLessThanOrEqualTo(2);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private long percentile(List<Long> values, int percentile) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }
}
