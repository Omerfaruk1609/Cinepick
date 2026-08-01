package proje.cinepick.api.controllers;

import org.springframework.web.bind.annotation.*;
import proje.cinepick.buisness.NarrativeService;
import proje.cinepick.dto.NarrativeRequest;
import proje.cinepick.dto.NarrativeResponse;

import java.util.List;

@RestController
@RequestMapping("/api/narrative")
@CrossOrigin(origins = "*")
public class NarrativeController {

    private final NarrativeService narrativeService;

    public NarrativeController(NarrativeService narrativeService) {
        this.narrativeService = narrativeService;
    }

    @PostMapping("/analyze")
    public NarrativeResponse analyze(@RequestBody NarrativeRequest request) {
        return narrativeService.analyzeNarrative(request);
    }

    @GetMapping("/analyze")
    public NarrativeResponse analyzeGet(
            @RequestParam(required = false) String overview,
            @RequestParam(required = false, defaultValue = "120") Integer runtime,
            @RequestParam(required = false, defaultValue = "2020") Integer releaseYear,
            @RequestParam(required = false, defaultValue = "8.0") Double voteAverage,
            @RequestParam(required = false) List<String> genres
    ) {
        NarrativeRequest request = new NarrativeRequest(overview, runtime, releaseYear, voteAverage, genres);
        return narrativeService.analyzeNarrative(request);
    }
}
