package proje.cinepick.benchmark;

import proje.cinepick.entity.Movie;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Offline evaluation metrics for the recommendation system.
 *
 * All methods are pure-function utilities — no Spring context needed.
 *
 * Formulas used:
 *  Precision@K  = |recommended ∩ relevant| / K
 *  Recall@K     = |recommended ∩ relevant| / |relevant|
 *  NDCG@K       = DCG@K / IDCG@K   (binary relevance: 1 if relevant, 0 otherwise)
 *  Diversity    = Genre Shannon entropy over top-K recommendations
 *  Novelty      = avg(1 - log10(voteCount+1) / maxLogVote)  → higher = more obscure
 *  Coverage     = |unique movies recommended across all users| / total catalog
 */
public class BenchmarkMetricsCalculator {

    // ─── Precision@K ─────────────────────────────────────────────────────────

    /**
     * @param recommendedIds ordered list of recommended movie IDs (size >= K)
     * @param relevantIds    ground-truth set of movie IDs the user would like
     * @param k              cut-off rank
     * @return precision at K  (0.0 – 1.0)
     */
    public static double precisionAtK(List<Long> recommendedIds, Set<Long> relevantIds, int k) {
        if (recommendedIds == null || relevantIds == null || relevantIds.isEmpty() || k <= 0) return 0.0;
        long hits = recommendedIds.stream().limit(k)
                .filter(relevantIds::contains)
                .count();
        return (double) hits / k;
    }

    // ─── Recall@K ────────────────────────────────────────────────────────────

    public static double recallAtK(List<Long> recommendedIds, Set<Long> relevantIds, int k) {
        if (recommendedIds == null || relevantIds == null || relevantIds.isEmpty() || k <= 0) return 0.0;
        long hits = recommendedIds.stream().limit(k)
                .filter(relevantIds::contains)
                .count();
        return (double) hits / relevantIds.size();
    }

    // ─── NDCG@K ──────────────────────────────────────────────────────────────

    public static double ndcgAtK(List<Long> recommendedIds, Set<Long> relevantIds, int k) {
        if (recommendedIds == null || relevantIds == null || relevantIds.isEmpty() || k <= 0) return 0.0;

        double dcg = 0.0;
        int limit = Math.min(k, recommendedIds.size());
        for (int i = 0; i < limit; i++) {
            double rel = relevantIds.contains(recommendedIds.get(i)) ? 1.0 : 0.0;
            dcg += rel / (Math.log(i + 2) / Math.log(2)); // log2(rank+1)
        }

        // Ideal DCG: all relevant items at the top
        double idcg = 0.0;
        int idealHits = Math.min(relevantIds.size(), k);
        for (int i = 0; i < idealHits; i++) {
            idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
        }

        return idcg == 0.0 ? 0.0 : dcg / idcg;
    }

    // ─── Genre Diversity (Shannon Entropy) ───────────────────────────────────

    /**
     * Calculates Shannon entropy over genre distribution.
     * Result is normalized to [0, 1] by dividing by log2(numGenres).
     *
     * Diversity = 0   → all recommendations same genre
     * Diversity = 1   → perfectly uniform genre distribution
     */
    public static double diversityScore(List<Movie> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) return 0.0;

        Map<String, Integer> genreCounts = new HashMap<>();
        int totalGenreOccurrences = 0;

        for (Movie m : recommendations) {
            if (m.getGenres() == null) continue;
            for (String genre : m.getGenres()) {
                genreCounts.merge(genre, 1, Integer::sum);
                totalGenreOccurrences++;
            }
        }

        if (genreCounts.isEmpty() || totalGenreOccurrences == 0) return 0.0;

        int numGenres = genreCounts.size();
        double entropy = 0.0;
        for (int count : genreCounts.values()) {
            double p = (double) count / totalGenreOccurrences;
            if (p > 0) entropy -= p * (Math.log(p) / Math.log(2));
        }

        double maxEntropy = Math.log(numGenres) / Math.log(2);
        return maxEntropy == 0 ? 0.0 : Math.min(1.0, entropy / maxEntropy);
    }

    // ─── Novelty Score (Obscurity) ────────────────────────────────────────────

    /**
     * Higher novelty = more obscure movies (lower vote counts).
     * Uses inverse log-normalized popularity.
     *
     * novelty = avg over movies of: 1 - log10(voteCount+1) / log10(maxVoteCount+1)
     */
    public static double noveltyScore(List<Movie> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) return 0.0;

        // Estimate max popularity (e.g., Avengers ~1M votes → log10 ≈ 6)
        double maxLogVote = Math.log10(1_000_000 + 1);

        double totalNovelty = recommendations.stream()
                .mapToDouble(m -> {
                    long votes = m.getVoteCount() != null ? m.getVoteCount() : 0L;
                    double logVote = Math.log10(votes + 1);
                    return 1.0 - (logVote / maxLogVote);
                })
                .sum();

        return totalNovelty / recommendations.size();
    }

    // ─── Catalog Coverage ────────────────────────────────────────────────────

    /**
     * @param allRecommendedIds set of unique movie IDs that were ever recommended
     * @param totalCatalogSize  total number of movies in the catalog
     * @return coverage ratio (0.0 – 1.0)
     */
    public static double coverageRatio(Set<Long> allRecommendedIds, long totalCatalogSize) {
        if (totalCatalogSize <= 0) return 0.0;
        return Math.min(1.0, (double) allRecommendedIds.size() / totalCatalogSize);
    }

    // ─── Serendipity Score ────────────────────────────────────────────────────

    /**
     * Serendipity = items that are unexpectedly relevant.
     * Approximated here as: items that are relevant BUT have low cosine similarity
     * to the user's historical preference vector (i.e., not "obvious" picks).
     *
     * @param recommendedIds  recommended movie IDs (ordered)
     * @param relevantIds     ground-truth relevant set
     * @param primitiveScores cosine similarity score per recommended movie (same order)
     * @param k               cut-off rank
     * @param similarityThresh items below this threshold are "surprising"
     */
    public static double serendipityAtK(List<Long> recommendedIds,
                                         Set<Long> relevantIds,
                                         List<Double> primitiveScores,
                                         int k,
                                         double similarityThresh) {
        if (recommendedIds == null || relevantIds == null || primitiveScores == null) return 0.0;
        int limit = Math.min(k, Math.min(recommendedIds.size(), primitiveScores.size()));
        long serendipitous = 0;
        for (int i = 0; i < limit; i++) {
            boolean isRelevant = relevantIds.contains(recommendedIds.get(i));
            boolean isSurprising = primitiveScores.get(i) < similarityThresh;
            if (isRelevant && isSurprising) serendipitous++;
        }
        return (double) serendipitous / k;
    }

    // ─── Taste Drift ─────────────────────────────────────────────────────────

    /**
     * Measures drift between two user preference vectors.
     * Uses cosine similarity: low value → high drift.
     */
    public static double cosineSimilarity(float[] v1, float[] v2) {
        if (v1 == null || v2 == null || v1.length == 0 || v2.length == 0) return 0.0;
        int len = Math.min(v1.length, v2.length);
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < len; i++) {
            dot   += v1[i] * v2[i];
            normA += v1[i] * v1[i];
            normB += v2[i] * v2[i];
        }
        if (normA <= 0 || normB <= 0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // ─── Benchmark Summary ────────────────────────────────────────────────────

    public record BenchmarkSummary(
            double precisionAt10,
            double recallAt10,
            double ndcgAt10,
            double diversityScore,
            double noveltyScore,
            double coverageRatio,
            int sampleSize
    ) {
        @Override
        public String toString() {
            return String.format("""
                    ╔══════════════════════════════════════════╗
                    ║   CinePick Recommendation Metrics        ║
                    ╠══════════════════════════════════════════╣
                    ║  Precision@10   : %.2f%%                ║
                    ║  Recall@10      : %.2f%%                ║
                    ║  NDCG@10        : %.4f                  ║
                    ║  Diversity      : %.4f (0.6–0.8 ideal) ║
                    ║  Novelty        : %.4f                  ║
                    ║  Coverage       : %.2f%%                ║
                    ║  Sample size    : %d users              ║
                    ╚══════════════════════════════════════════╝
                    """,
                    precisionAt10 * 100, recallAt10 * 100,
                    ndcgAt10, diversityScore, noveltyScore,
                    coverageRatio * 100, sampleSize);
        }
    }
}
