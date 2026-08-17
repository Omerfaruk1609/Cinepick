package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import proje.cinepick.service.CatalogIngestionService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class CatalogIngestionController {

    private final CatalogIngestionService catalogIngestionService;

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, String>> triggerCatalogIngest() {
        catalogIngestionService.ingestCatalog();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Catalog ingestion completed and database titles cleaned."
        ));
    }

    @RequestMapping(
            value = {"/bulk-import-5k", "/bulk-import-15k"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ResponseEntity<Map<String, String>> triggerBulkImport() {
        catalogIngestionService.triggerBulkImport15kAsync();
        return ResponseEntity.ok(Map.of(
                "status", "started",
                "message", "5.000+ film toplu içe aktarım ve Türkiye yayın sağlayıcıları çekim işlemi arka planda başlatıldı."
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getDatabaseMovieCount() {
        long count = catalogIngestionService.getMovieCount();
        return ResponseEntity.ok(Map.of(
                "totalMoviesInDatabase", count,
                "targetGoal", 5000,
                "status", count >= 5000 ? "completed" : "ingesting"
        ));
    }
}
