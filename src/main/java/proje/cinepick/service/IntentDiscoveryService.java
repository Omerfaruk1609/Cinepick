package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.IntentDiscoveryRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntentDiscoveryService {

    private final LocalEmbeddingService localEmbeddingService;
    private final MovieRepository movieRepository;

    private static final Map<String, String[]> GENRE_KEYWORDS = Map.ofEntries(
            Map.entry("Bilim Kurgu", new String[]{"bilim kurgu", "bilimkurgu", "sci-fi", "uzay", "robot", "yapay zeka", "mars", "zaman yolculu", "distopya", "fütüristik", "evren", "galaksi", "uzaylı"}),
            Map.entry("Aksiyon", new String[]{"aksiyon", "dövüş", "çatışma", "patlama", "hızlı", "macera", "ajan", "özel tim", "operasyon", "intikam", "süper kahraman"}),
            Map.entry("Komedi", new String[]{"komedi", "eğlenceli", "komik", "güldüren", "kahkaha", "mizah", "gırgır", "şenlik", "parodi"}),
            Map.entry("Korku", new String[]{"korku", "korkunç", "gerilim", "paranormal", "hayalet", "cin", "katil", "kanlı", "slasher", "zombi", "karanlık", "kabus"}),
            Map.entry("Gerilim", new String[]{"gerilim", "heyecan", "kaçış", "adrenalin", "tüyler ürpertici", "nefes kesen", "stres", "klostrofobik", "panik"}),
            Map.entry("Gizem", new String[]{"gizem", "akıl oyunları", "ters köşe", "twist", "dedektif", "cinayet", "soruşturma", "sır", "bulmaca", "şüpheli", "beyin yakan"}),
            Map.entry("Dram", new String[]{"dram", "duygusal", "hüzünlü", "ağlatan", "gözyaşı", "yaşam", "aile", "çöküş", "psikolojik", "gerçek hayat", "acı"}),
            Map.entry("Romantik", new String[]{"romantik", "aşk", "sevgi", "sevgili", "evlilik", "ilişki", "flört", "romans", "rom-com"}),
            Map.entry("Animasyon", new String[]{"animasyon", "çizgi film", "anime", "çizgi", "pixar", "disney", "ghibli"}),
            Map.entry("Suç", new String[]{"suç", "mafya", "gangster", "soygun", "hırsız", "hapishane", "uyuşturucu", "kartel", "polis", "yeraltı"}),
            Map.entry("Tarih", new String[]{"tarih", "tarihi", "dönem", "osmanlı", "orta çağ", "antik", "roma", "sultan", "krallık", "biyografi"}),
            Map.entry("Savaş", new String[]{"savaş", "asker", "cephe", "ordu", "tank", "dünya savaşı", "muharebe", "vietnam", "çatışma"}),
            Map.entry("Macera", new String[]{"macera", "keşif", "yolculuk", "hazine", "orman", "deniz", "dağ", "arkeoloji", "arayış"}),
            Map.entry("Fantezi", new String[]{"fantezi", "fantastik", "büyü", "büyücü", "ejderha", "elf", "yüzük", "orta dünya", "efsane", "mitoloji"})
    );

    public List<MovieDto> discoverByIntent(IntentDiscoveryRequest request) {
        if (request == null || request.getEffectiveQuery() == null || request.getEffectiveQuery().trim().isEmpty()) {
            throw new IllegalArgumentException("Query/Prompt cannot be empty for intent discovery");
        }

        String searchPrompt = request.getEffectiveQuery().trim();
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 20;
        String lowerQuery = searchPrompt.toLowerCase(Locale.forLanguageTag("tr"));

        log.info("🔍 Performing intelligent intent discovery for prompt: '{}' with limit {}", searchPrompt, limit);

        // 1. NLP Analizi: Yıl / Dönem Tespiti
        Integer targetMinYear = null;
        Integer targetMaxYear = null;
        String decadeLabel = null;

        if (lowerQuery.contains("90'lar") || lowerQuery.contains("90lar") || lowerQuery.contains("doksanlar") || lowerQuery.contains("90'lı") || lowerQuery.contains("90li")) {
            targetMinYear = 1990; targetMaxYear = 1999; decadeLabel = "90'lar";
        } else if (lowerQuery.contains("80'ler") || lowerQuery.contains("80ler") || lowerQuery.contains("seksenler") || lowerQuery.contains("80'li")) {
            targetMinYear = 1980; targetMaxYear = 1989; decadeLabel = "80'ler";
        } else if (lowerQuery.contains("70'ler") || lowerQuery.contains("70ler") || lowerQuery.contains("yetmişler")) {
            targetMinYear = 1970; targetMaxYear = 1979; decadeLabel = "70'ler";
        } else if (lowerQuery.contains("2000'ler") || lowerQuery.contains("2000ler") || lowerQuery.contains("2000'li")) {
            targetMinYear = 2000; targetMaxYear = 2009; decadeLabel = "2000'ler";
        } else if (lowerQuery.contains("2010'lar") || lowerQuery.contains("2010lar") || lowerQuery.contains("2010'lu")) {
            targetMinYear = 2010; targetMaxYear = 2019; decadeLabel = "2010'lar";
        } else if (lowerQuery.contains("2020'ler") || lowerQuery.contains("yeni çıkan") || lowerQuery.contains("güncel") || lowerQuery.contains("son çıkan")) {
            targetMinYear = 2020; targetMaxYear = 2026; decadeLabel = "2020'ler";
        }

        // 2. Dil / Ülke Tespiti
        String targetLanguage = null;
        String languageLabel = null;
        if (lowerQuery.contains("türk") || lowerQuery.contains("türkçe") || lowerQuery.contains("yerli") || lowerQuery.contains("yeşilçam")) {
            targetLanguage = "tr";
            languageLabel = "Türk Sineması";
        } else if (lowerQuery.contains("yabancı") || lowerQuery.contains("hollywood")) {
            targetLanguage = "foreign";
            languageLabel = "Yabancı Sinema";
        } else if (lowerQuery.contains("kore") || lowerQuery.contains("korean") || lowerQuery.contains("kdrama")) {
            targetLanguage = "ko";
            languageLabel = "Kore Sineması";
        } else if (lowerQuery.contains("japon") || lowerQuery.contains("anime")) {
            targetLanguage = "ja";
            languageLabel = "Japon Sineması";
        }

        // 3. Hedef Türlerin Çıkarımı
        Set<String> matchedTargetGenres = new HashSet<>();
        for (Map.Entry<String, String[]> entry : GENRE_KEYWORDS.entrySet()) {
            for (String kw : entry.getValue()) {
                if (lowerQuery.contains(kw)) {
                    matchedTargetGenres.add(entry.getKey());
                    break;
                }
            }
        }

        // 4. Anahtar Kelimeleri Çıkar (Stop words hariç)
        Set<String> stopWords = Set.of("bir", "ve", "ile", "için", "olan", "gibi", "film", "filmi", "filmleri", "tarzı", "türünde", "hakkında", "öner", "bana", "tavsiye", "istiyorum", "güzel", "en", "iyi");
        List<String> queryTokens = Arrays.stream(lowerQuery.split("[\\s,;:.!?]+"))
                .filter(t -> t.length() >= 3 && !stopWords.contains(t))
                .toList();

        // 5. Veritabanından Aday Havuzunu Çek
        List<Movie> candidatePool;
        String[] genreArray = matchedTargetGenres.isEmpty() ? null : matchedTargetGenres.toArray(new String[0]);
        candidatePool = movieRepository.filterMovies(
                null,
                genreArray,
                targetLanguage,
                targetMinYear,
                targetMaxYear,
                5.0,
                null,
                null,
                500,
                0
        );

        if (candidatePool.isEmpty()) {
            candidatePool = movieRepository.findPopularMovies(500);
        }

        // 6. Çok Kriterli Niyet Skorlaması & Eşleşme Oranı Hesaplama
        final Integer finalMinYear = targetMinYear;
        final Integer finalMaxYear = targetMaxYear;
        final String finalLanguage = targetLanguage;
        final String finalDecadeLabel = decadeLabel;
        final String finalLanguageLabel = languageLabel;

        List<ScoredMovie> scoredList = new ArrayList<>();

        for (Movie movie : candidatePool) {
            double score = 0.0;
            List<String> matchHighlights = new ArrayList<>();

            String titleLower = (movie.getTitle() != null) ? movie.getTitle().toLowerCase(Locale.forLanguageTag("tr")) : "";
            String overviewLower = (movie.getOverview() != null) ? movie.getOverview().toLowerCase(Locale.forLanguageTag("tr")) : "";
            Set<String> movieGenres = movie.getGenres() != null ? new HashSet<>(Arrays.asList(movie.getGenres())) : Collections.emptySet();

            // A) Anahtar Kelime Eşleşmeleri
            int tokenHits = 0;
            for (String token : queryTokens) {
                if (titleLower.contains(token)) {
                    score += 35.0;
                    tokenHits++;
                } else if (overviewLower.contains(token)) {
                    score += 15.0;
                    tokenHits++;
                }
            }
            if (tokenHits > 0) {
                matchHighlights.add("Aradığınız tema ve olay örgüsüyle");
            }

            // B) Tür Uyumu
            int genreHits = 0;
            for (String targetG : matchedTargetGenres) {
                if (movieGenres.contains(targetG)) {
                    score += 25.0;
                    genreHits++;
                }
            }
            if (genreHits > 0) {
                matchHighlights.add(String.join("/", matchedTargetGenres) + " türü");
            }

            // C) Dönem / Yıl Uyumu
            if (finalMinYear != null && movie.getReleaseYear() != null) {
                if (movie.getReleaseYear() >= finalMinYear && movie.getReleaseYear() <= finalMaxYear) {
                    score += 30.0;
                    matchHighlights.add(finalDecadeLabel + " dönemi");
                }
            }

            // D) Dil Uyumu
            if (finalLanguage != null) {
                if ("tr".equals(finalLanguage) && "tr".equalsIgnoreCase(movie.getOriginalLanguage())) {
                    score += 25.0;
                    matchHighlights.add(finalLanguageLabel);
                } else if ("foreign".equals(finalLanguage) && !"tr".equalsIgnoreCase(movie.getOriginalLanguage())) {
                    score += 15.0;
                }
            }

            // E) Kalite & Puan Ağırlığı (Bayesian Rating Bonus: Max 20 puan)
            double voteAvg = movie.getVoteAverage() != null ? movie.getVoteAverage() : 6.0;
            long voteCnt = movie.getVoteCount() != null ? movie.getVoteCount() : 100;
            double bayesianRating = ((voteCnt * voteAvg) + (200 * 6.5)) / (voteCnt + 200);
            score += (bayesianRating / 10.0) * 15.0;

            // Normalize Match Percentage (70% - 99%)
            int matchPercentage = (int) Math.min(99, Math.max(72, 70 + (score * 0.35)));

            // Anlamlı Niyet Açıklaması Üretimi
            String reason;
            if (!matchHighlights.isEmpty()) {
                reason = String.format("%s ile kusursuz uyum sağlayan başyapıt (%% %d)", String.join(", ", matchHighlights), matchPercentage);
            } else {
                reason = String.format("Arama niyetiniz ve yüksek izleyici beğenisi ile öne çıkan seçki (%% %d)", matchPercentage);
            }

            scoredList.add(new ScoredMovie(movie, score, matchPercentage, reason));
        }

        // En yüksek skora göre sırala ve limit kadar döndür
        return scoredList.stream()
                .sorted(Comparator.comparingDouble(ScoredMovie::score).reversed())
                .limit(limit)
                .map(sm -> {
                    MovieDto dto = MovieDto.fromEntity(sm.movie());
                    dto.setMatchPercentage(sm.matchPercentage());
                    dto.setRecommendationReason(sm.reason());
                    return dto;
                })
                .toList();
    }

    private record ScoredMovie(Movie movie, double score, int matchPercentage, String reason) {}
}
