package proje.cinepick.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import proje.cinepick.repository.MovieRepository;
import proje.cinepick.service.CatalogIngestionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMovieCatalogSeeder implements ApplicationRunner {

    private final MovieRepository movieRepository;
    private final CatalogIngestionService catalogIngestionService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        long currentCount = movieRepository.count();
        if (currentCount >= 5000) {
            log.info("Database already contains {} authentic TMDB movies. Ingestion skipped.", currentCount);
            return;
        }

        log.info("Current movie count in database: {}. Initiating/Resuming 5.000 Real TMDB Movies Ingestion...", currentCount);
        catalogIngestionService.triggerBulkImport15kAsync();
    }
}
