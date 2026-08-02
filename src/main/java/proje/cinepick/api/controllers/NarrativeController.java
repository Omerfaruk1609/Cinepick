package proje.cinepick.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;
import proje.cinepick.service.MovieEmbeddingService;
import proje.cinepick.service.SpringAiNarrativeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/narrative")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NarrativeController {

    private final SpringAiNarrativeService springAiNarrativeService;
    private final MovieEmbeddingService movieEmbeddingService;

    @PostMapping("/analyze-ai")
    public ResponseEntity<MovieAnalysisResponse> analyzeAi(@RequestBody MovieAnalysisRequest request) {
        MovieAnalysisRequest safeRequest = request != null ? request : new MovieAnalysisRequest();
        MovieAnalysisResponse response = springAiNarrativeService.analyzeMovie(safeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/similar")
    public ResponseEntity<List<Long>> findSimilarMovies(@RequestParam String query, @RequestParam(defaultValue = "5") int topK) {
        List<Long> movieIds = movieEmbeddingService.findTopKSimilarMovies(query, topK);
        return ResponseEntity.ok(movieIds);
    }
}
