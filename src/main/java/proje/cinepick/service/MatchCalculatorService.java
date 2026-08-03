package proje.cinepick.service;

import org.springframework.stereotype.Service;

@Service
public class MatchCalculatorService {

    public int calculateMatchPercentage(double vectorSimilarity, boolean hasGenreMatch, Double voteAverage) {
        double vote = voteAverage != null ? voteAverage : 7.0;

        // 1. Bileşenlerin normalize ağırlıkları
        double simWeight = vectorSimilarity * 0.70;
        double genreWeight = (hasGenreMatch ? 0.15 : 0.0) * 0.20;
        double baseWeight = (vote / 10.0) * 0.10;

        // 2. Ham skor (0.0 - 1.0 arası)
        double rawScore = simWeight + genreWeight + baseWeight;

        // 3. Yüzdeye çevirme (0 - 100)
        int percentage = (int) Math.round(rawScore * 100);

        // 4. Sınırlandırma (Clamp: min %50, max %99)
        if (percentage > 99) return 99;
        if (percentage < 50) return 50;

        return percentage;
    }
}
