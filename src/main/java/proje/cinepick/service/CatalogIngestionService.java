package proje.cinepick.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proje.cinepick.dto.tmdb.TmdbPageResponse;
import proje.cinepick.dto.tmdb.TmdbWatchProviderResponse;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogIngestionService {

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final LocalEmbeddingService localEmbeddingService;

    @Value("${tmdb.api.key:dummy_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api.url:https://api.themoviedb.org/3}")
    private String tmdbApiUrl;

    private static final Map<Integer, String> TMDB_GENRE_MAP = Map.ofEntries(
            Map.entry(28, "Aksiyon"),
            Map.entry(12, "Macera"),
            Map.entry(16, "Animasyon"),
            Map.entry(35, "Komedi"),
            Map.entry(80, "Suç"),
            Map.entry(99, "Belgesel"),
            Map.entry(18, "Dram"),
            Map.entry(10751, "Aile"),
            Map.entry(14, "Fantezi"),
            Map.entry(36, "Tarih"),
            Map.entry(27, "Korku"),
            Map.entry(10402, "Müzik"),
            Map.entry(9648, "Gizem"),
            Map.entry(10749, "Romantik"),
            Map.entry(878, "Bilim Kurgu"),
            Map.entry(10770, "TV Filmi"),
            Map.entry(53, "Gerilim"),
            Map.entry(10752, "Savaş"),
            Map.entry(37, "Vahşi Batı")
    );

    public CompletableFuture<Void> triggerBulkImport15kAsync() {
        return CompletableFuture.runAsync(this::bulkImport15kMovies);
    }

    public CompletableFuture<Void> triggerBulkImport5kAsync() {
        return CompletableFuture.runAsync(this::bulkImport15kMovies);
    }

    public Map<String, Object> bulkImport15kMovies() {
        log.info("🚀 Starting 15.000+ Real Movies TMDB Ingestion Engine across all categories...");
        long startTime = System.currentTimeMillis();

        List<String> queryEndpoints = new ArrayList<>(List.of(
                "/movie/popular?language=tr-TR&page=%d",
                "/movie/top_rated?language=tr-TR&page=%d",
                "/movie/now_playing?language=tr-TR&page=%d",
                "/movie/upcoming?language=tr-TR&page=%d",
                "/discover/movie?language=tr-TR&sort_by=popularity.desc&page=%d",
                "/discover/movie?language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?language=tr-TR&sort_by=revenue.desc&page=%d",
                // Türk Sineması (Popüler, Puan, Tarih)
                "/discover/movie?with_original_language=tr&language=tr-TR&sort_by=popularity.desc&page=%d",
                "/discover/movie?with_original_language=tr&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_original_language=tr&language=tr-TR&sort_by=primary_release_date.desc&page=%d",
                // Tüm Türler Bireysel
                "/discover/movie?with_genres=28&language=tr-TR&sort_by=vote_count.desc&page=%d", // Aksiyon
                "/discover/movie?with_genres=12&language=tr-TR&sort_by=vote_count.desc&page=%d", // Macera
                "/discover/movie?with_genres=16&language=tr-TR&sort_by=vote_count.desc&page=%d", // Animasyon
                "/discover/movie?with_genres=35&language=tr-TR&sort_by=vote_count.desc&page=%d", // Komedi
                "/discover/movie?with_genres=80&language=tr-TR&sort_by=vote_count.desc&page=%d", // Suç
                "/discover/movie?with_genres=99&language=tr-TR&sort_by=vote_count.desc&page=%d", // Belgesel
                "/discover/movie?with_genres=18&language=tr-TR&sort_by=vote_count.desc&page=%d", // Dram
                "/discover/movie?with_genres=10751&language=tr-TR&sort_by=vote_count.desc&page=%d", // Aile
                "/discover/movie?with_genres=14&language=tr-TR&sort_by=vote_count.desc&page=%d", // Fantezi
                "/discover/movie?with_genres=36&language=tr-TR&sort_by=vote_count.desc&page=%d", // Tarih
                "/discover/movie?with_genres=27&language=tr-TR&sort_by=vote_count.desc&page=%d", // Korku
                "/discover/movie?with_genres=10402&language=tr-TR&sort_by=vote_count.desc&page=%d", // Müzik
                "/discover/movie?with_genres=9648&language=tr-TR&sort_by=vote_count.desc&page=%d", // Gizem
                "/discover/movie?with_genres=10749&language=tr-TR&sort_by=vote_count.desc&page=%d", // Romantik
                "/discover/movie?with_genres=878&language=tr-TR&sort_by=vote_count.desc&page=%d", // Bilim Kurgu
                "/discover/movie?with_genres=53&language=tr-TR&sort_by=vote_count.desc&page=%d", // Gerilim
                "/discover/movie?with_genres=10752&language=tr-TR&sort_by=vote_count.desc&page=%d", // Savaş
                "/discover/movie?with_genres=37&language=tr-TR&sort_by=vote_count.desc&page=%d" // Vahşi Batı
        ));

        // Yıllara göre en iyi filmler (1970 - 2026)
        for (int y = 2026; y >= 1970; y--) {
            queryEndpoints.add(String.format("/discover/movie?primary_release_year=%d&language=tr-TR&sort_by=vote_count.desc&page=%%d", y));
        }

        Set<Long> processedTmdbIds = new HashSet<>(movieRepository.findAllTmdbIds());
        log.info("Initialized ingestion cache with {} existing database movie IDs.", processedTmdbIds.size());
        int totalSaved = 0;
        int totalUpdated = 0;

        for (String endpointTemplate : queryEndpoints) {
            long currentDbCount = movieRepository.count();
            if (currentDbCount >= 5000) {
                log.info("Reached target 5.000 real movies limit! (Total in DB: {})", currentDbCount);
                break;
            }

            for (int page = 1; page <= 40; page++) {
                if (movieRepository.count() >= 5000) {
                    break;
                }

                try {
                    String separator = endpointTemplate.contains("?") ? "&" : "?";
                    String url = String.format("%s%s%sapi_key=%s",
                            tmdbApiUrl, String.format(endpointTemplate, page), separator, tmdbApiKey);

                    TmdbPageResponse response = restTemplate.getForObject(url, TmdbPageResponse.class);

                    if (response != null && response.getResults() != null) {
                        for (TmdbPageResponse.TmdbItem item : response.getResults()) {
                            if (item.getId() != null && !processedTmdbIds.contains(item.getId())) {
                                boolean isNew = processAndSaveMovieWithProviders(item);
                                processedTmdbIds.add(item.getId());
                                if (isNew) {
                                    totalSaved++;
                                    if (totalSaved % 50 == 0) {
                                        log.info("🎬 Ingested {} new real movies. Total in DB: {}", totalSaved, movieRepository.count());
                                    }
                                } else {
                                    totalUpdated++;
                                }

                                if (movieRepository.count() >= 5000) {
                                    break;
                                }

                                try {
                                    Thread.sleep(15);
                                } catch (InterruptedException ignored) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Error fetching TMDB page: {} on template: {} - {}", page, endpointTemplate, e.getMessage());
                }
            }
        }

        cleanMovieTitlesInDatabase();
        long durationSec = (System.currentTimeMillis() - startTime) / 1000;
        log.info("🎉 Bulk 15K Ingestion finished in {} seconds. Distinct Movies Processed: {}, Saved New: {}, Updated: {}",
                durationSec, processedTmdbIds.size(), totalSaved, totalUpdated);

        return Map.of(
                "status", "completed",
                "durationSeconds", durationSec,
                "distinctMoviesProcessed", processedTmdbIds.size(),
                "savedNew", totalSaved,
                "updated", totalUpdated
        );
    }

    public Map<String, Object> bulkImport5kMovies() {
        return bulkImport15kMovies();
    }

    public void ingestCatalog() {
        bulkImport15kMovies();
    }

    private boolean processAndSaveMovieWithProviders(TmdbPageResponse.TmdbItem item) {
        if (item.getId() == null || item.getTitle() == null) return false;

        String cleanedTitle = cleanTitle(item.getTitle());
        Optional<Movie> existingOpt = movieRepository.findByTmdbId(item.getId());
        boolean isNew = existingOpt.isEmpty();

        Movie movie = existingOpt.orElseGet(() -> Movie.builder().tmdbId(item.getId()).build());

        movie.setTitle(cleanedTitle);
        movie.setPosterPath(item.getPosterPath());
        if (item.getReleaseDate() != null && !item.getReleaseDate().isBlank()) {
            try {
                movie.setReleaseDate(java.time.LocalDate.parse(item.getReleaseDate()));
                if (item.getReleaseDate().length() >= 4) {
                    movie.setReleaseYear(Integer.parseInt(item.getReleaseDate().substring(0, 4)));
                }
            } catch (Exception ignored) {}
        }
        movie.setVoteAverage(item.getVoteAverage());
        movie.setVoteCount(item.getVoteCount());
        movie.setOriginalLanguage(item.getOriginalLanguage());

        // Türleri Türkçeleştir ve Ata
        if (item.getGenreIds() != null && !item.getGenreIds().isEmpty()) {
            List<String> genreNames = item.getGenreIds().stream()
                    .map(TMDB_GENRE_MAP::get)
                    .filter(Objects::nonNull)
                    .toList();
            if (!genreNames.isEmpty()) {
                movie.setGenres(genreNames.toArray(new String[0]));
            }
        }

        // 1. Türkçe / İngilizce Overview Fallback
        String overview = item.getOverview();
        if (overview == null || overview.trim().isEmpty()) {
            overview = fetchEnglishOverviewFallback(item.getId());
        }
        movie.setOverview(overview);

        // 2. Watch Providers (Türkiye Flatrate Yayın Platformları)
        String platforms = fetchTurkeyStreamingPlatforms(item.getId());
        movie.setStreamingPlatforms(platforms);

        // 3. Local ONNX 384-D Vector Embedding Üretimi
        if (overview != null && !overview.isBlank()) {
            try {
                String textForEmbedding = String.format("%s %s %s",
                        cleanedTitle,
                        movie.getGenres() != null ? String.join(" ", movie.getGenres()) : "",
                        overview);
                float[] embedding = localEmbeddingService.generateEmbedding(textForEmbedding);
                movie.setEmbedding(embedding);
            } catch (Exception e) {
                log.debug("Embedding error for TMDB ID {}: {}", item.getId(), e.getMessage());
            }
        }

        movieRepository.save(movie);
        return isNew;
    }

    private String fetchEnglishOverviewFallback(Long tmdbId) {
        try {
            String url = String.format("%s/movie/%d?api_key=%s&language=en-US", tmdbApiUrl, tmdbId, tmdbApiKey);
            TmdbPageResponse.TmdbItem detail = restTemplate.getForObject(url, TmdbPageResponse.TmdbItem.class);
            if (detail != null && detail.getOverview() != null && !detail.getOverview().isBlank()) {
                return detail.getOverview();
            }
        } catch (Exception ignored) {}
        return "Sinematik hikaye ve karakter anlatımı içeren seçkin yapım.";
    }

    private String fetchTurkeyStreamingPlatforms(Long tmdbId) {
        try {
            String url = String.format("%s/movie/%d/watch/providers?api_key=%s", tmdbApiUrl, tmdbId, tmdbApiKey);
            TmdbWatchProviderResponse response = restTemplate.getForObject(url, TmdbWatchProviderResponse.class);

            if (response != null && response.getResults() != null && response.getResults().containsKey("TR")) {
                TmdbWatchProviderResponse.CountryProviders tr = response.getResults().get("TR");
                if (tr != null && tr.getFlatrate() != null && !tr.getFlatrate().isEmpty()) {
                    return tr.getFlatrate().stream()
                            .map(TmdbWatchProviderResponse.ProviderDetail::getProviderName)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.joining(","));
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public String cleanTitle(String title) {
        if (title == null) return null;
        return title.replaceAll("(?i)\\s+(Vol\\.\\s*\\d+|\\d+)$", "").trim();
    }

    public void cleanMovieTitlesInDatabase() {
        log.info("Executing database title cleanup for 'Vol. X' suffixes...");
        List<Movie> allMovies = movieRepository.findAll();
        int cleanedCount = 0;

        for (Movie movie : allMovies) {
            if (movie.getTitle() != null) {
                String cleaned = cleanTitle(movie.getTitle());
                if (!cleaned.equals(movie.getTitle())) {
                    movie.setTitle(cleaned);
                    movieRepository.save(movie);
                    cleanedCount++;
                }
            }
        }
        log.info("Cleaned {} movie titles in database.", cleanedCount);
    }
}
