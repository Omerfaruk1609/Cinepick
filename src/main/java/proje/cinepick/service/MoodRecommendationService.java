package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MoodRequest;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoodRecommendationService {

    private final LocalEmbeddingService localEmbeddingService;
    private final MovieRepository movieRepository;

    private static final Map<String, List<String>> MOOD_GENRE_MAPPING = Map.of(
            "energetic", List.of("Aksiyon", "Macera", "Gerilim"),
            "melancholic", List.of("Dram", "Romantik"),
            "tense", List.of("Gizem", "Gerilim", "Korku", "Suç"),
            "romantic", List.of("Romantik", "Komedi", "Dram"),
            "thoughtful", List.of("Bilim Kurgu", "Belgesel", "Gizem", "Dram"),
            "cheerful", List.of("Komedi", "Animasyon", "Aile")
    );

    public List<MovieDto> recommendByMood(MoodRequest request) {
        if (request == null) {
            request = new MoodRequest();
        }

        String queryText = buildMoodQueryText(request);
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 20;

        log.info("Generating mood recommendations for query: '{}' with limit: {}", queryText, limit);

        float[] moodVector = localEmbeddingService.generateEmbedding(queryText);
        String vectorString = Arrays.toString(moodVector);

        List<String> boostGenres = getBoostGenres(request.getMoodTag());
        String[] genreArray = (boostGenres != null && !boostGenres.isEmpty()) ? boostGenres.toArray(new String[0]) : null;

        List<Movie> matchedMovies = movieRepository.filterMovies(
                vectorString,
                genreArray,
                null,
                null,
                null,
                null,
                null,
                null,
                limit,
                0
        );

        return matchedMovies.stream()
                .map(MovieDto::fromEntity)
                .toList();
    }

    private String buildMoodQueryText(MoodRequest request) {
        if (request.getMoodText() != null && !request.getMoodText().trim().isEmpty()) {
            return request.getMoodText();
        }
        if (request.getMoodTag() != null && !request.getMoodTag().trim().isEmpty()) {
            return switch (request.getMoodTag().toLowerCase()) {
                case "energetic" -> "enerjik adrenalin aksiyon macera hızlı tempolu energetic high adrenaline action adventure fast paced";
                case "melancholic" -> "melankolik duygusal dokunaklı derin dram hüzünlü melancholic emotional touching deeply moving drama";
                case "tense" -> "gerilim gizem karanlık psikolojik gerilim suç suspenseful psychological thriller dark mystery";
                case "romantic" -> "romantik sıcak samimi aşk tatlı hissettiren romantic heartwarming sweet love story feel good";
                case "thoughtful" -> "zihin büken felsefi düşündürücü derin bilim kurgu gizem thought-provoking philosophical mind bending complex sci-fi";
                case "cheerful" -> "neşeli eğlenceli komik kahkaha pozitif neşe cheerful funny uplifting hilarious lighthearted comedy";
                default -> request.getMoodTag();
            };
        }
        return "ilham verici ve büyüleyici sinematik yolculuk inspiring and captivating cinematic journey";
    }

    private List<String> getBoostGenres(String moodTag) {
        if (moodTag == null) return List.of();
        return MOOD_GENRE_MAPPING.getOrDefault(moodTag.toLowerCase(), List.of());
    }
}
