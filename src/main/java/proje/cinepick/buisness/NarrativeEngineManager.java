package proje.cinepick.buisness;

import org.springframework.stereotype.Service;
import proje.cinepick.buisness.analyzers.AtmosphereAnalyzer;
import proje.cinepick.buisness.analyzers.PaceAnalyzer;
import proje.cinepick.buisness.analyzers.ThemeExtractor;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

import java.util.List;

@Service
public class NarrativeEngineManager implements NarrativeEngineService {

    private final AtmosphereAnalyzer atmosphereAnalyzer;
    private final PaceAnalyzer paceAnalyzer;
    private final ThemeExtractor themeExtractor;

    public NarrativeEngineManager(AtmosphereAnalyzer atmosphereAnalyzer,
                                  PaceAnalyzer paceAnalyzer,
                                  ThemeExtractor themeExtractor) {
        this.atmosphereAnalyzer = atmosphereAnalyzer;
        this.paceAnalyzer = paceAnalyzer;
        this.themeExtractor = themeExtractor;
    }

    @Override
    public MovieAnalysisResponse analyzeMovie(MovieAnalysisRequest request) {
        if (request == null) {
            request = new MovieAnalysisRequest();
        }

        String title = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle()
                : "Bu eser";
        String overview = (request.getOverview() != null && !request.getOverview().isBlank())
                ? request.getOverview()
                : "Film hakkında derin atmosferik detaylar barındıran sinematik bir yapıt.";

        String atmosphere = atmosphereAnalyzer.analyze(overview, request.getGenres());
        String narrativePace = paceAnalyzer.analyze(request.getRuntime(), request.getReleaseYear());
        List<String> keyThemes = themeExtractor.extractThemes(overview, request.getGenres());

        String whyToWatch = generateWhyToWatch(title, atmosphere, narrativePace, keyThemes, request.getVoteAverage());

        return MovieAnalysisResponse.builder()
                .atmosphere(atmosphere)
                .narrativePace(narrativePace)
                .keyThemes(keyThemes)
                .whyToWatch(whyToWatch)
                .build();
    }

    private String generateWhyToWatch(String title, String atmosphere, String narrativePace, List<String> keyThemes, Double voteAverage) {
        double score = (voteAverage != null && voteAverage > 0) ? voteAverage : 8.0;
        String themeText = String.join(", ", keyThemes);

        StringBuilder sb = new StringBuilder();
        sb.append(title).append(", ").append(atmosphere.toLowerCase()).append(" tonu ve ").append(narrativePace.toLowerCase()).append(" ile dikkat çekiyor. ");
        sb.append("Film boyunca ").append(themeText).append(" gibi temalar izleyiciyi derin düşünsel bir yolculuğa çıkarır. ");

        if (score >= 8.0) {
            sb.append("Zengin kurgusu ve ").append(score).append(" seviyesindeki sinematik başarısıyla derin anlam arayışında olan izleyicilerin kaçırmaması gereken özel bir eserdir.");
        } else {
            sb.append("Özgün yapısı ve etkileyici görsel anlatımıyla farklı bir sinematik deneyim arayanlar için tavsiye edilir.");
        }

        return sb.toString();
    }
}
