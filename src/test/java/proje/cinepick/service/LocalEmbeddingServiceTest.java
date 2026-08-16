package proje.cinepick.service;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalEmbeddingServiceTest {

    private OrtEnvironment ortEnvironment;

    @Mock
    private OrtSession ortSession;

    @Mock
    private HuggingFaceTokenizer tokenizer;

    @Mock
    private Encoding encoding;

    @Mock
    private OrtSession.Result ortResult;

    private LocalEmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        ortEnvironment = OrtEnvironment.getEnvironment();
        embeddingService = new LocalEmbeddingService(ortEnvironment, ortSession, tokenizer);
    }

    @Test
    void generateEmbedding_NullOrEmptyText_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> embeddingService.generateEmbedding(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");

        assertThatThrownBy(() -> embeddingService.generateEmbedding("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    void generateEmbedding_UnloadedBeans_ReturnsFallbackNormalizedVector() {
        LocalEmbeddingService uninitializedService = new LocalEmbeddingService(null, null, null);
        float[] fallbackVector = uninitializedService.generateEmbedding("valid text");
        assertThat(fallbackVector).isNotNull();
        assertThat(fallbackVector.length).isEqualTo(384);

        double normSq = 0.0;
        for (float val : fallbackVector) {
            normSq += val * val;
        }
        double magnitude = Math.sqrt(normSq);
        assertThat(magnitude).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void generateEmbedding_ValidInput_Returns384DimNormalizedVector() throws Exception {
        long[] fakeIds = new long[]{101, 2054, 2001, 102};
        long[] fakeMask = new long[]{1, 1, 1, 1};
        long[] fakeTypeIds = new long[]{0, 0, 0, 0};

        when(tokenizer.encode("Inception sci-fi masterpiece")).thenReturn(encoding);
        when(encoding.getIds()).thenReturn(fakeIds);
        when(encoding.getAttentionMask()).thenReturn(fakeMask);
        when(encoding.getTypeIds()).thenReturn(fakeTypeIds);

        float[][][] fakeOutput = new float[1][4][384];
        for (int i = 0; i < 4; i++) {
            for (int d = 0; d < 384; d++) {
                fakeOutput[0][i][d] = 0.5f;
            }
        }

        OnnxTensor fakeTensor = OnnxTensor.createTensor(ortEnvironment, fakeOutput);

        when(ortSession.run(any(Map.class))).thenReturn(ortResult);
        when(ortResult.get(0)).thenReturn(fakeTensor);

        float[] embedding = embeddingService.generateEmbedding("Inception sci-fi masterpiece");

        assertThat(embedding).isNotNull();
        assertThat(embedding.length).isEqualTo(384);

        double normSq = 0.0;
        for (float val : embedding) {
            normSq += val * val;
        }
        double magnitude = Math.sqrt(normSq);
        assertThat(magnitude).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.001));
    }
}
