package proje.cinepick.benchmark;

import org.junit.jupiter.api.Test;
import proje.cinepick.util.VectorMathUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelBenchmarkRunner {

    @Test
    void benchmarkVectorMathOperations() {
        float[] v1 = new float[384];
        float[] v2 = new float[384];
        for (int i = 0; i < 384; i++) {
            v1[i] = (float) Math.sin(i);
            v2[i] = (float) Math.cos(i);
        }

        int iterations = 10000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            VectorMathUtil.cosineSimilarity(v1, v2);
            VectorMathUtil.normalizeL2(v1);
        }

        long durationNs = System.nanoTime() - startTime;
        double durationMs = durationNs / 1_000_000.0;

        System.out.printf("🚀 Benchmark: %d vector operations completed in %.3f ms (%.4f ms/op)%n",
                iterations, durationMs, durationMs / iterations);

        assertThat(durationMs).isLessThan(1000.0);
    }
}
