package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.dto.TasteAnalyticsDto;
import proje.cinepick.entity.Movie;
import proje.cinepick.entity.UserMovieInteraction;
import proje.cinepick.repository.UserMovieInteractionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TasteAnalyticsService {

    private final UserMovieInteractionRepository interactionRepository;

    @Transactional(readOnly = true)
    public TasteAnalyticsDto getUserTasteAnalytics(Long userId) {
        List<UserMovieInteraction> interactions = interactionRepository.findTop20ByUserIdOrderByUpdatedAtDesc(userId);
        if (interactions.isEmpty()) {
            interactions = interactionRepository.findByUserIdAndIsFavoriteTrue(userId);
        }

        if (interactions.isEmpty()) {
            return TasteAnalyticsDto.builder()
                    .totalMoviesWatched(0)
                    .obscurityScore(50.0)
                    .cinemaPersona("Henüz Yeterli Veri Yok")
                    .topGenres(Collections.emptyMap())
                    .topDirectors(Collections.emptyMap())
                    .build();
        }

        List<Movie> movies = interactions.stream()
                .map(UserMovieInteraction::getMovie)
                .filter(Objects::nonNull)
                .toList();

        if (movies.isEmpty()) {
            return TasteAnalyticsDto.builder()
                    .totalMoviesWatched(0)
                    .obscurityScore(50.0)
                    .cinemaPersona("Henüz Yeterli Veri Yok")
                    .topGenres(Collections.emptyMap())
                    .topDirectors(Collections.emptyMap())
                    .build();
        }

        // 1. Obscurity Skoru Hesaplama (TMDB Vote Count ortalaması baz alınır)
        // Düşük oy sayısı = Yüksek Obscurity (Bağımsız Sinema)
        double avgVoteCount = movies.stream()
                .mapToLong(m -> m.getVoteCount() != null ? m.getVoteCount() : 1000L)
                .average().orElse(10000.0);

        // Normalize edilmiş Obscurity (Popüler filmler 50,000+ oya sahiptir)
        double obscurityScore = Math.max(0, Math.min(100, 100 - (avgVoteCount / 500.0)));

        // 2. Sinema Karakteri (Persona) Belirleme
        String persona = determinePersona(obscurityScore, movies);

        // 3. En Çok İzlenen Türler (Dağılım)
        Map<String, Integer> genreCounts = new HashMap<>();
        for (Movie movie : movies) {
            if (movie.getGenres() != null) {
                for (String genre : movie.getGenres()) {
                    genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
                }
            }
        }

        // 4. En Çok İzlenen Yönetmenler
        Map<String, Integer> directorCounts = movies.stream()
                .filter(m -> m.getDirector() != null && !m.getDirector().isBlank())
                .collect(Collectors.groupingBy(Movie::getDirector, Collectors.summingInt(e -> 1)));

        return TasteAnalyticsDto.builder()
                .totalMoviesWatched(movies.size())
                .obscurityScore(Math.round(obscurityScore * 10.0) / 10.0)
                .cinemaPersona(persona)
                .topGenres(sortByValueAndLimit(genreCounts, 5))
                .topDirectors(sortByValueAndLimit(directorCounts, 3))
                .build();
    }

    private String determinePersona(double obscurity, List<Movie> movies) {
        if (obscurity > 75) return "Gizli Cevher Avcısı (Indie Cinephile)";
        if (obscurity > 50) return "Dengeli Sinefil (Eclectic Viewer)";
        if (obscurity > 25) return "Popüler Trend Takipçisi";
        return "Blockbuster & Ana Akım Tutkunu";
    }

    private Map<String, Integer> sortByValueAndLimit(Map<String, Integer> map, int limit) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
