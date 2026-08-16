package proje.cinepick.util;

public class VectorMathUtil {

    /**
     * Vector addition of two float arrays.
     */
    public static float[] addVectors(float[] v1, float[] v2) {
        if (v1 == null && v2 == null) return null;
        if (v1 == null) return v2.clone();
        if (v2 == null) return v1.clone();
        int len = Math.min(v1.length, v2.length);
        float[] result = new float[len];
        for (int i = 0; i < len; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    /**
     * Multiplies a vector by a scalar float.
     */
    public static float[] scalarMultiply(float[] vector, float scalar) {
        if (vector == null) return null;
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * scalar;
        }
        return result;
    }

    /**
     * Normalizes a vector using L2 norm.
     */
    public static float[] normalizeL2(float[] vector) {
        if (vector == null) return null;
        double sumSquares = 0.0;
        for (float val : vector) {
            sumSquares += val * val;
        }
        double magnitude = Math.sqrt(sumSquares);
        float[] normalized = new float[vector.length];
        if (magnitude > 0) {
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = (float) (vector[i] / magnitude);
            }
        }
        return normalized;
    }

    /**
     * Time-decay weighting formula: w_i = e^(-lambda * delta_t_days)
     *
     * @param daysDiff difference in days between interaction time and current time
     * @param lambda decay rate parameter (e.g. 0.01)
     * @return time decay weight factor
     */
    public static float calculateTimeDecayWeight(double daysDiff, double lambda) {
        if (daysDiff < 0) daysDiff = 0;
        return (float) Math.exp(-lambda * daysDiff);
    }

    /**
     * Calculates the weighted centroid of multiple vectors and applies L2 normalization.
     */
    public static float[] calculateWeightedCentroid(float[][] vectors, float[] weights) {
        if (vectors == null || vectors.length == 0 || vectors[0] == null) {
            return null;
        }

        int dimension = vectors[0].length;
        float[] centroid = new float[dimension];

        for (int i = 0; i < vectors.length; i++) {
            float weight = weights[i];
            float[] vector = vectors[i];
            if (vector == null) continue;
            for (int d = 0; d < Math.min(dimension, vector.length); d++) {
                centroid[d] += vector[d] * weight;
            }
        }

        return normalizeL2(centroid);
    }

    /**
     * Calculates Cosine Similarity between two vectors and maps result to range [0.0, 1.0].
     */
    public static double cosineSimilarity(float[] v1, float[] v2) {
        if (v1 == null || v2 == null || v1.length == 0 || v2.length == 0) {
            return 0.5;
        }
        int len = Math.min(v1.length, v2.length);
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < len; i++) {
            dot += v1[i] * v2[i];
            normA += v1[i] * v1[i];
            normB += v2[i] * v2[i];
        }
        if (normA <= 0 || normB <= 0) return 0.5;
        double sim = dot / (Math.sqrt(normA) * Math.sqrt(normB));
        return Math.max(0.0, Math.min(1.0, (sim + 1.0) / 2.0));
    }
}
