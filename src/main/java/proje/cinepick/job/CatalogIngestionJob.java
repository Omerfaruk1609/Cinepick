package proje.cinepick.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import proje.cinepick.service.CatalogIngestionService;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CatalogIngestionJob {

    private final CatalogIngestionService catalogIngestionService;

    /**
     * Scheduled job to ingest catalog data from TMDB every Sunday at 03:00 AM.
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void runWeeklyCatalogIngestion() {
        log.info("Triggering weekly TMDB catalog ingestion background job...");
        try {
            catalogIngestionService.ingestCatalog();
        } catch (Exception e) {
            log.error("Error occurred during weekly catalog ingestion job", e);
        }
    }
}
