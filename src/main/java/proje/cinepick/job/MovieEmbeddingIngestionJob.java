package proje.cinepick.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import proje.cinepick.entity.Movie;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.service.MovieEmbeddingService;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MovieEmbeddingIngestionJob {

    private final MovieRepository movieRepository;
    private final MovieEmbeddingService movieEmbeddingService;

    private static final int BATCH_SIZE = 50;

    @Scheduled(fixedRateString = "${cinepick.jobs.embedding-ingestion.rate-ms:300000}")
    public void processUnindexedMoviesBatch() {
        log.info("Starting scheduled movie vector embedding ingestion job...");

        try {
            List<Movie> unindexedMovies = movieRepository.findUnindexedMovies(PageRequest.of(0, BATCH_SIZE));

            if (unindexedMovies.isEmpty()) {
                log.info("No unindexed movies found. Vector database is up-to-date.");
                return;
            }

            log.info("Found {} unindexed movies. Beginning vectorization batch...", unindexedMovies.size());
            int successCount = 0;

            for (Movie movie : unindexedMovies) {
                try {
                    if (movie.getOverview() != null && !movie.getOverview().isBlank()) {
                        movieEmbeddingService.saveMovieEmbedding(movie.getTmdbId(), movie.getOverview());
                        successCount++;
                    }
                } catch (Exception ex) {
                    log.error("Failed to process embedding for movie ID {}: {}", movie.getTmdbId(), ex.getMessage());
                }
            }

            log.info("Batch embedding ingestion finished. Successfully indexed {}/{} movies.", successCount, unindexedMovies.size());
        } catch (Exception e) {
            log.warn("Movie embedding ingestion job skipped: {}", e.getMessage());
        }
    }
}
