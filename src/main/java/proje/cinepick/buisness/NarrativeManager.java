package proje.cinepick.buisness;

import org.springframework.stereotype.Service;
import proje.cinepick.dto.NarrativeRequest;
import proje.cinepick.dto.NarrativeResponse;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class NarrativeManager implements NarrativeService {

    private static final Pattern PSYCHOLOGICAL = Pattern.compile("(?i).*(gizem|katil|psikoloji|sır|karanlık|cinayet|rüy|zihin|akıl).*");
    private static final Pattern SCI_FI = Pattern.compile("(?i).*(uzay|gelecek|bilim|yapay|gezegen|zaman|robot|teknoloji).*");
    private static final Pattern DRAMA_PHILOSOPHY = Pattern.compile("(?i).*(hayat|varoluş|ölüm|sorgula|felsefe|inanç|trajed|vicdan).*");
    private static final Pattern ACTION_CHAOS = Pattern.compile("(?i).*(savaş|patlama|kaçış|intikam|suç|silah|dövüş|mücadele).*");

    @Override
    public NarrativeResponse analyzeNarrative(NarrativeRequest request) {
        String overview = request.getOverview() != null ? request.getOverview() : "";
        Integer runtime = request.getRuntime() != null ? request.getRuntime() : 110;
        Integer releaseYear = request.getReleaseYear() != null ? request.getReleaseYear() : 2020;
        Double vote = request.getVoteAverage() != null ? request.getVoteAverage() : 8.0;
        List<String> genres = request.getGenres() != null ? request.getGenres() : List.of();

        String vibe = determineVibe(overview, genres);
        String pace = determinePace(runtime, releaseYear);
        String insight = generateInsight(vibe, pace, vote, overview);
        double depthScore = Math.min(9.9, Math.max(6.0, (vote * 0.8) + (runtime > 120 ? 1.2 : 0.8)));

        return new NarrativeResponse(vibe, pace, insight, Math.round(depthScore * 10.0) / 10.0);
    }

    private String determineVibe(String overview, List<String> genres) {
        String genreStr = String.join(" ", genres).toLowerCase();

        if (PSYCHOLOGICAL.matcher(overview).matches() || genreStr.contains("gizem") || genreStr.contains("mystery")) {
            return "Kasvetli & Felsefi";
        } else if (SCI_FI.matcher(overview).matches() || genreStr.contains("bilim") || genreStr.contains("sci-fi")) {
            return "Sürreal & Düşündürücü";
        } else if (ACTION_CHAOS.matcher(overview).matches() || genreStr.contains("aksiyon") || genreStr.contains("action")) {
            return "Adrenalin & Sinematik Kaos";
        } else if (DRAMA_PHILOSOPHY.matcher(overview).matches() || genreStr.contains("dram") || genreStr.contains("drama")) {
            return "Duygusal & Varoluşsal Derinlik";
        }
        return "Atmosferik & Etkileyici";
    }

    private String determinePace(Integer runtime, Integer releaseYear) {
        if (releaseYear != null && releaseYear < 1990) {
            return "Klasik & Ağırbaşlı Anlatı Ritmi";
        }
        if (runtime != null) {
            if (runtime >= 135) {
                return "Yavaş Salınımlı ve Katmanlı Anlatı";
            } else if (runtime <= 100) {
                return "Hızlı, Akıcı ve Doğrusal Kurgu";
            }
        }
        return "Dengeli ve Sürükleyici Anlatı Temposu";
    }

    private String generateInsight(String vibe, String pace, Double vote, String overview) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bu yapım, ").append(vibe.toLowerCase()).append(" atmosferiyle öne çıkıyor. ");
        sb.append(pace).append(" sunarak seyirciyi karakterlerin iç dünyasına çeken güçlü bir sinematik deneyim vaat eder. ");

        if (vote >= 8.0) {
            sb.append("Eleştirmenler ve izleyiciler tarafından yüksek puanla taçlandırılmış bu eseri, derin anlam arayışında olan sinemaseverler mutlaka izlemelidir.");
        } else {
            sb.append("Özgün kurgusu ve görsel estetiğiyle farklı bir atmosfer keşfetmek isteyenler için dikkat çekici bir tercih.");
        }

        return sb.toString();
    }
}
