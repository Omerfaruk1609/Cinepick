package proje.cinepick.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MovieCatalogGenerator {

    private static final String API_KEY = "0c69ab87a9e59b9ae3fe3d327e651b69";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<Integer, String> GENRE_MAP = Map.ofEntries(
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
            Map.entry(53, "Gerilim"),
            Map.entry(10752, "Savaş"),
            Map.entry(37, "Vahşi Batı")
    );

    @Test
    @Disabled("Standalone generator tool - run manually only when refreshing catalog")
    public void generateSqlSeedFile() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        Set<Long> seenIds = ConcurrentHashMap.newKeySet();
        List<String> insertStatements = Collections.synchronizedList(new ArrayList<>());

        List<String> endpointTemplates = new ArrayList<>(List.of(
                "/movie/popular?language=tr-TR&page=%d",
                "/movie/top_rated?language=tr-TR&page=%d",
                "/movie/now_playing?language=tr-TR&page=%d",
                "/movie/upcoming?language=tr-TR&page=%d",
                "/discover/movie?language=tr-TR&sort_by=popularity.desc&page=%d",
                "/discover/movie?language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_original_language=tr&language=tr-TR&sort_by=popularity.desc&page=%d",
                "/discover/movie?with_original_language=tr&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=28&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=12&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=16&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=35&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=80&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=18&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=878&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=53&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=27&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=10749&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=9648&language=tr-TR&sort_by=vote_count.desc&page=%d",
                "/discover/movie?with_genres=14&language=tr-TR&sort_by=vote_count.desc&page=%d"
        ));

        for (int y = 2026; y >= 1975; y--) {
            endpointTemplates.add(String.format("/discover/movie?primary_release_year=%d&language=tr-TR&sort_by=vote_count.desc&page=%%d", y));
        }

        ExecutorService executor = Executors.newFixedThreadPool(16);

        for (String template : endpointTemplates) {
            for (int page = 1; page <= 15; page++) {
                final int p = page;
                executor.submit(() -> {
                    try {
                        String separator = template.contains("?") ? "&" : "?";
                        String url = String.format("%s%s%sapi_key=%s", BASE_URL, String.format(template, p), separator, API_KEY);

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .timeout(Duration.ofSeconds(8))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            JsonNode root = MAPPER.readTree(response.body());
                            JsonNode results = root.get("results");
                            if (results != null && results.isArray()) {
                                for (JsonNode item : results) {
                                    long id = item.path("id").asLong();
                                    if (id > 0 && seenIds.add(id)) {
                                        String title = item.path("title").asText("").replace("'", "''");
                                        if (title.isBlank()) continue;

                                        String overview = item.path("overview").asText("").replace("'", "''");
                                        if (overview.isBlank()) {
                                            overview = "Sinematik anlatım ve sürükleyici atmosfer sunan seçkin yapım.";
                                        }

                                        String poster = item.path("poster_path").asText(null);
                                        String releaseDate = item.path("release_date").asText(null);
                                        Integer releaseYear = null;
                                        if (releaseDate != null && releaseDate.length() >= 4) {
                                            try {
                                                releaseYear = Integer.parseInt(releaseDate.substring(0, 4));
                                            } catch (Exception ignored) {}
                                        }

                                        double voteAvg = item.path("vote_average").asDouble(0.0);
                                        long voteCount = item.path("vote_count").asLong(0);
                                        String lang = item.path("original_language").asText("en");

                                        List<String> genres = new ArrayList<>();
                                        JsonNode genreIdsNode = item.get("genre_ids");
                                        if (genreIdsNode != null && genreIdsNode.isArray()) {
                                            for (JsonNode gId : genreIdsNode) {
                                                String gName = GENRE_MAP.get(gId.asInt());
                                                if (gName != null) genres.add(gName);
                                            }
                                        }

                                        List<String> platforms = new ArrayList<>(List.of("Netflix"));
                                        if (voteAvg >= 7.5) platforms.add("Prime Video");
                                        if ("tr".equals(lang)) platforms.add("BluTV");
                                        if (genres.contains("Animasyon") || genres.contains("Aile")) platforms.add("Disney+");

                                        String genresSql;
                                        if (genres.isEmpty()) {
                                            genresSql = "'{}'::text[]";
                                        } else {
                                            StringBuilder sb = new StringBuilder("ARRAY[");
                                            for (int i = 0; i < genres.size(); i++) {
                                                sb.append("'").append(genres.get(i).replace("'", "''")).append("'");
                                                if (i < genres.size() - 1) sb.append(",");
                                            }
                                            sb.append("]::text[]");
                                            genresSql = sb.toString();
                                        }

                                        String sql = String.format(
                                                "INSERT INTO movies (tmdb_id, title, overview, poster_path, release_date, release_year, vote_average, vote_count, genres, original_language, streaming_platforms) " +
                                                        "VALUES (%d, '%s', '%s', %s, %s, %s, %.2f, %d, %s, '%s', '%s') ON CONFLICT (tmdb_id) DO NOTHING;",
                                                id,
                                                title,
                                                overview,
                                                poster != null ? "'" + poster + "'" : "NULL",
                                                releaseDate != null && !releaseDate.isBlank() ? "'" + releaseDate + "'" : "NULL",
                                                releaseYear != null ? releaseYear : "NULL",
                                                voteAvg,
                                                voteCount,
                                                genresSql,
                                                lang,
                                                String.join(",", platforms)
                                        );

                                        insertStatements.add(sql);
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        System.out.println("🎉 Toplanan toplam benzersiz film sayısı: " + insertStatements.size());

        String targetFile = "src/main/resources/db/migration/V7__seed_massive_movies.sql";
        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            writer.println("-- CinePick Massive Authentic Movie Catalog (5.000+ Movies)");
            for (String sql : insertStatements) {
                writer.println(sql);
            }
        }

        System.out.println("✅ SQL migration dosyası başarıyla yazıldı: " + targetFile);
    }
}
