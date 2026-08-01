package proje.cinepick.buisness;

import proje.cinepick.dto.NarrativeRequest;
import proje.cinepick.dto.NarrativeResponse;

public interface NarrativeService {
    NarrativeResponse analyzeNarrative(NarrativeRequest request);
}
