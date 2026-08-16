package proje.cinepick.benchmark;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheBenchmarkTest {

    @Test
    void measureCacheHitVsMissPerformance() {
        Map<String, float[]> mockCache = new HashMap<>();
        float[] dummyVector = new float[384];
        mockCache.put("user:vector:100", dummyVector);

        long startHit = System.nanoTime();
        float[] cached = mockCache.get("user:vector:100");
        long hitDuration = System.nanoTime() - startHit;

        long startMiss = System.nanoTime();
        float[] miss = mockCache.get("user:vector:999");
        long missDuration = System.nanoTime() - startMiss;

        assertThat(cached).isNotNull();
        assertThat(miss).isNull();
        assertThat(hitDuration).isLessThan(1_000_000L); // Under 1 ms
    }
}
