package proje.cinepick.buisness.analyzers;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class AtmosphereAnalyzer {

    private static final Pattern PSYCHOLOGICAL = Pattern.compile("(?i).*(gizem|katil|psikoloji|sır|karanlık|cinayet|rüy|zihin|akıl).*");
    private static final Pattern SCI_FI = Pattern.compile("(?i).*(uzay|gelecek|bilim|yapay|gezegen|zaman|robot|teknoloji|ütopya|distopya).*");
    private static final Pattern ACTION_CHAOS = Pattern.compile("(?i).*(savaş|patlama|kaçış|intikam|suç|silah|dövüş|mücadele).*");
    private static final Pattern DRAMA_PHILOSOPHY = Pattern.compile("(?i).*(hayat|varoluş|ölüm|sorgula|felsefe|inanç|trajed|vicdan).*");

    public String analyze(String overview, List<String> genres) {
        String text = overview != null ? overview : "";
        String genreStr = genres != null ? String.join(" ", genres).toLowerCase() : "";

        if (PSYCHOLOGICAL.matcher(text).find() || genreStr.contains("gizem") || genreStr.contains("mystery")) {
            return "Kasvetli, Zihin Bükücü & Felsefi";
        } else if (SCI_FI.matcher(text).find() || genreStr.contains("bilim") || genreStr.contains("sci-fi")) {
            return "Sürreal, Ütopyanın Gölgesinde & Düşündürücü";
        } else if (ACTION_CHAOS.matcher(text).find() || genreStr.contains("aksiyon") || genreStr.contains("action")) {
            return "Sinematik, Yüksek Gerilimli & Tempolu";
        } else if (DRAMA_PHILOSOPHY.matcher(text).find() || genreStr.contains("dram") || genreStr.contains("drama")) {
            return "Duygusal, Varoluşçu & Derinlikli";
        }
        return "Atmosferik, Şiirsel & Etkileyici";
    }
}
