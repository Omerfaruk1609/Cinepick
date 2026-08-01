package proje.cinepick.api.controllers;

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
    public MovieAnalysisResponse analyze(@RequestBody MovieAnalysisRequest request) {
        return narrativeEngineService.analyzeMovie(request);
    }
}
