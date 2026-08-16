package proje.cinepick.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @org.springframework.web.bind.annotation.GetMapping({"/bulk-import-5k", "/bulk-import-15k"})
    @PostMapping({"/bulk-import-5k", "/bulk-import-15k"})
    public ResponseEntity<Map<String, String>> triggerBulkImport() {
        catalogIngestionService.triggerBulkImport15kAsync();
        return ResponseEntity.ok(Map.of(
                "status", "started",
                "message", "5.000+ film toplu içe aktarım ve Türkiye yayın sağlayıcıları çekim işlemi arka planda başlatıldı."
        ));
    }
}
