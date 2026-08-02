package proje.cinepick.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proje.cinepick.buisness.NarrativeEngineService;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

@RestController
@RequestMapping("/api/narrative")
@CrossOrigin(origins = "*")
public class NarrativeController {

    private final NarrativeEngineService narrativeEngineService;

    public NarrativeController(NarrativeEngineService narrativeEngineService) {
        this.narrativeEngineService = narrativeEngineService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<MovieAnalysisResponse> analyze(@RequestBody(required = false) MovieAnalysisRequest request) {
        MovieAnalysisResponse response = narrativeEngineService.analyzeMovie(request != null ? request : new MovieAnalysisRequest());
        return ResponseEntity.ok(response);
    }
}
