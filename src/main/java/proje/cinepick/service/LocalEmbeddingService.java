package proje.cinepick.service;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocalEmbeddingService {

    private final OrtEnvironment ortEnvironment;
    private final OrtSession ortSession;
    private final HuggingFaceTokenizer tokenizer;

    @Autowired
    public LocalEmbeddingService(@Autowired(required = false) OrtEnvironment ortEnvironment,
                                 @Autowired(required = false) OrtSession ortSession,
                                 @Autowired(required = false) HuggingFaceTokenizer tokenizer) {
        this.ortEnvironment = ortEnvironment;
        this.ortSession = ortSession;
        this.tokenizer = tokenizer;
    }

    /**
     * Generates a 384-dimensional normalized embedding for the input text using local ONNX model.
     *
     * @param text input text (movie overview/description)
     * @return 384-dimensional L2-normalized float array
     */
    public float[] generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty");
        }

        // Safety truncation for overly long text inputs
        if (text.length() > 2000) {
            text = text.substring(0, 2000);
        }

        if (ortSession == null || tokenizer == null || ortEnvironment == null) {
            return generateFallbackVector(text);
        }

        Encoding encoding = tokenizer.encode(text);
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();
        long[] typeIds = encoding.getTypeIds();

        long[] shape = new long[]{1, inputIds.length};

        try {
            OnnxTensor inputIdsTensor = OnnxTensor.createTensor(ortEnvironment, LongBuffer.wrap(inputIds), shape);
            OnnxTensor attentionMaskTensor = OnnxTensor.createTensor(ortEnvironment, LongBuffer.wrap(attentionMask), shape);
            OnnxTensor tokenTypeIdsTensor = OnnxTensor.createTensor(ortEnvironment, LongBuffer.wrap(typeIds), shape);

            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("input_ids", inputIdsTensor);
            inputs.put("attention_mask", attentionMaskTensor);
            inputs.put("token_type_ids", tokenTypeIdsTensor);

            try (OrtSession.Result result = ortSession.run(inputs)) {
                Object rawValue = result.get(0).getValue();
                float[][][] tokenEmbeddings = (float[][][]) rawValue;

                float[] pooled = meanPooling(tokenEmbeddings[0], attentionMask);
                return l2Normalize(pooled);
            } finally {
                inputIdsTensor.close();
                attentionMaskTensor.close();
                tokenTypeIdsTensor.close();
            }
        } catch (OrtException e) {
            throw new RuntimeException("Failed to run ONNX inference for embedding generation", e);
        }
    }

    private float[] meanPooling(float[][] tokenEmbeddings, long[] attentionMask) {
        int seqLength = tokenEmbeddings.length;
        int hiddenSize = tokenEmbeddings[0].length;
        float[] sum = new float[hiddenSize];
        int validTokenCount = 0;

        for (int i = 0; i < seqLength; i++) {
            if (attentionMask[i] == 1L) {
                validTokenCount++;
                for (int d = 0; d < hiddenSize; d++) {
                    sum[d] += tokenEmbeddings[i][d];
                }
            }
        }

        float[] pooled = new float[hiddenSize];
        if (validTokenCount > 0) {
            for (int d = 0; d < hiddenSize; d++) {
                pooled[d] = sum[d] / validTokenCount;
            }
        }
        return pooled;
    }

    private float[] l2Normalize(float[] vector) {
        double normSq = 0.0;
        for (float val : vector) {
            normSq += val * val;
        }
        double norm = Math.sqrt(normSq);
        float[] normalized = new float[vector.length];
        if (norm > 0.0) {
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = (float) (vector[i] / norm);
            }
        }
        return normalized;
    }

    private float[] generateFallbackVector(String text) {
        float[] vector = new float[384];
        long seed = text.hashCode();
        java.util.Random random = new java.util.Random(seed);
        for (int i = 0; i < 384; i++) {
            vector[i] = (float) random.nextGaussian();
        }
        return l2Normalize(vector);
    }
}
