package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieDto;
import proje.cinepick.dto.MovieWizardRequest;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieWizardService {

    private final LocalEmbeddingService localEmbeddingService;
    private final MovieRepository movieRepository;

    public List<MovieDto> discoverByWizard(MovieWizardRequest request) {
        if (request == null) {
            request = new MovieWizardRequest();
        }

        // 1. Dil (Origin) Filtresi
        String originalLanguage = null;
        if ("TR".equalsIgnoreCase(request.getOrigin())) {
            originalLanguage = "tr";
        } else if ("FOREIGN".equalsIgnoreCase(request.getOrigin())) {
            originalLanguage = "foreign";
        }

        // 2. Dönem (Era) Filtresi
        Integer minYear = null;
        Integer maxYear = null;
        if ("CLASSIC".equalsIgnoreCase(request.getEra())) {
            maxYear = 1999;
        } else if ("MODERN".equalsIgnoreCase(request.getEra())) {
            minYear = 2000;
            maxYear = 2019;
        } else if ("NEW".equalsIgnoreCase(request.getEra())) {
            minYear = 2020;
        }

        // 3. Tempo / Süre (Pace) Filtresi
        Integer maxRuntime = null;
        if ("FAST".equalsIgnoreCase(request.getPace())) {
            maxRuntime = 110;
        }

        // 4. Platform Filtresi
        String platform = null;
        if (request.getPlatforms() != null && !request.getPlatforms().isEmpty()) {
            platform = request.getPlatforms().get(0);
        }

        // 5. Prompt & Mood Vektörleştirme
        String promptText = buildWizardPromptText(request);
        log.info("MovieWizard running with prompt: '{}', origin: {}, era: {}, pace: {}, platform: {}",
                promptText, originalLanguage, request.getEra(), request.getPace(), platform);

        float[] promptVector = localEmbeddingService.generateEmbedding(promptText);
        String vectorString = Arrays.toString(promptVector);

        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 10;

        List<Movie> matchedMovies = movieRepository.filterMovies(
                vectorString,
                null,
                originalLanguage,
                minYear,
                maxYear,
                null,
                maxRuntime,
                platform,
                limit,
                0
        );

        return matchedMovies.stream()
                .map(movie -> {
                    MovieDto dto = MovieDto.fromEntity(movie);
                    dto.setMatchPercentage(Math.min(99, Math.max(82, (int) Math.round(75 + (movie.getVoteAverage() != null ? movie.getVoteAverage() * 2.5 : 20)))));
                    dto.setRecommendationReason("Sihirbaz kriterlerinize ve yapay zeka semantik eşleşmesine göre seçildi.");
                    return dto;
                })
                .toList();
    }

    private String buildWizardPromptText(MovieWizardRequest request) {
        StringBuilder sb = new StringBuilder();

        if (request.getMood() != null && !request.getMood().isBlank()) {
            String moodDesc = switch (request.getMood().toLowerCase()) {
                case "energetic" -> "high adrenaline energetic thrilling action packed adventure";
                case "melancholic" -> "deep emotional touching melancholic heartfelt drama";
                case "tense" -> "tense suspenseful psychological mystery crime thriller";
                case "romantic" -> "romantic heartwarming sweet cozy love story";
                case "thoughtful" -> "thought-provoking philosophical mind bending complex sci-fi";
                case "cheerful" -> "cheerful funny uplifting hilarious lighthearted comedy";
                default -> request.getMood();
            };
            sb.append(moodDesc).append(" ");
        }

        if (request.getCustomPrompt() != null && !request.getCustomPrompt().isBlank()) {
            sb.append(request.getCustomPrompt()).append(" ");
        }

        if ("FAST".equalsIgnoreCase(request.getPace())) {
            sb.append("dynamic fast paced gripping ");
        } else if ("SLOW".equalsIgnoreCase(request.getPace())) {
            sb.append("slow burn atmospheric contemplative artistic ");
        }

        String result = sb.toString().trim();
        return result.isEmpty() ? "captivating and acclaimed cinematic masterpiece" : result;
    }
}
