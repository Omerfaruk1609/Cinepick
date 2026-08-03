package proje.cinepick.util;

public class VectorMathUtil {

    public static float[] calculateWeightedCentroid(float[][] vectors, float[] weights) {
        if (vectors == null || vectors.length == 0 || vectors[0] == null) {
            return null;
        }

        int dimension = vectors[0].length; // pgvector varsayılan boyutu (1536)
        float[] centroid = new float[dimension];

        // 1. Ağırlıklı Toplam
        for (int i = 0; i < vectors.length; i++) {
            float weight = weights[i];
            float[] vector = vectors[i];
            if (vector == null) continue;
            for (int d = 0; d < Math.min(dimension, vector.length); d++) {
                centroid[d] += vector[d] * weight;
            }
        }

        // 2. Normalizasyon (L2 Norm)
        float sumSquares = 0.0f;
        for (float val : centroid) {
            sumSquares += val * val;
        }
        float magnitude = (float) Math.sqrt(sumSquares);

        if (magnitude > 0) {
            for (int d = 0; d < dimension; d++) {
                centroid[d] /= magnitude;
            }
        }

        return centroid;
    }

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
