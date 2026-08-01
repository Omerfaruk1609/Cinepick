package proje.cinepick.buisness;

import org.springframework.stereotype.Service;
import proje.cinepick.dto.MovieAnalysisRequest;
import proje.cinepick.dto.MovieAnalysisResponse;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class NarrativeEngineManager implements NarrativeEngineService {

    private static final Pattern PSYCHOLOGICAL = Pattern.compile("(?i).*(gizem|katil|psikoloji|sır|karanlık|cinayet|rüy|zihin|akıl).*");
    private static final Pattern SCI_FI = Pattern.compile("(?i).*(uzay|gelecek|bilim|yapay|gezegen|zaman|robot|teknoloji).*");
    private static final Pattern ACTION_CHAOS = Pattern.compile("(?i).*(savaş|patlama|kaçış|intikam|suç|silah|dövüş|mücadele).*");
    private static final Pattern DRAMA_PHILOSOPHY = Pattern.compile("(?i).*(hayat|varoluş|ölüm|sorgula|felsefe|inanç|trajed|vicdan).*");

    @Override
    public MovieAnalysisResponse analyzeMovie(MovieAnalysisRequest request) {
        String title = request.getTitle() != null ? request.getTitle() : "Bu eser";
        String overview = request.getOverview() != null ? request.getOverview() : "";
        Integer runtime = request.getRuntime() != null ? request.getRuntime() : 115;
        Integer releaseYear = request.getReleaseYear() != null ? request.getReleaseYear() : 2020;
        Double voteAverage = request.getVoteAverage() != null ? request.getVoteAverage() : 8.0;
        List<String> genres = request.getGenres() != null ? request.getGenres() : List.of();

        String atmosphere = determineAtmosphere(overview, genres);
        String narrativePace = determinePace(runtime, releaseYear);
        String whyToWatch = generateWhyToWatch(title, atmosphere, narrativePace, voteAverage);

        return new MovieAnalysisResponse(atmosphere, narrativePace, whyToWatch);
    }

    private String determineAtmosphere(String overview, List<String> genres) {
        String genreStr = String.join(" ", genres).toLowerCase();

        if (PSYCHOLOGICAL.matcher(overview).find() || genreStr.contains("gizem") || genreStr.contains("mystery")) {
            return "Kasvetli & Felsefi";
        } else if (SCI_FI.matcher(overview).find() || genreStr.contains("bilim") || genreStr.contains("sci-fi")) {
            return "Sürreal & Düşündürücü";
        } else if (ACTION_CHAOS.matcher(overview).find() || genreStr.contains("aksiyon") || genreStr.contains("action")) {
            return "Sinematik & Tempolu";
        } else if (DRAMA_PHILOSOPHY.matcher(overview).find() || genreStr.contains("dram") || genreStr.contains("drama")) {
            return "Duygusal & Varoluşsal Derinlik";
        }
        return "Atmosferik & Etkileyici";
    }

    private String determinePace(Integer runtime, Integer releaseYear) {
        if (releaseYear != null && releaseYear < 1990) {
            return "Yavaş Salınımlı Klasik Anlatı";
        }
        if (runtime != null) {
            if (runtime >= 135) {
                return "Katmanlı ve Geniş Zamana Yayılan Kurgu";
            } else if (runtime <= 100) {
                return "Hızlı ve Doğrusal Kurgu";
            }
        }
        return "Dengeli ve Sürükleyici Anlatı Temposu";
    }

    private String generateWhyToWatch(String title, String atmosphere, String narrativePace, Double voteAverage) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(", ").append(atmosphere.toLowerCase()).append(" tonu ve ").append(narrativePace.toLowerCase()).append(" ile sinemaseverlere özgün bir atmosfer yaşatır. ");
        
        if (voteAverage >= 8.0) {
            sb.append("Derin alt metinleri ve yüksek izleyici puanıyla zihinsel bir keşif arayanların kaçırmaması gereken başyapıtlardan biridir.");
        } else {
            sb.append("Farklı kurgusu ve güçlü sahne tasarımlarıyla sıra dışı bir sinema deneyimi sunuyor.");
        }

        return sb.toString();
    }
}
