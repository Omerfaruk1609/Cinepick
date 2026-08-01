package proje.cinepick.buisness;

import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

public interface NarrativeEngineService {
    MovieAnalysisResponse analyzeMovie(MovieAnalysisRequest request);
}
