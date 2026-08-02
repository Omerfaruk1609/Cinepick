package proje.cinepick.buisness.analyzers;

import org.springframework.stereotype.Component;

@Component
public class PaceAnalyzer {

    public String analyze(Integer runtime, Integer releaseYear) {
        int duration = (runtime != null && runtime > 0) ? runtime : 110;
        String durationSuffix = " (" + duration + " dk)";

        if (releaseYear != null && releaseYear < 1990) {
            return "Yavaş Salınımlı & Klasik Kurgu" + durationSuffix;
        }

        if (duration < 95) {
            return "Dinamik, Odaklı & Hızlı Tempolu" + durationSuffix;
        } else if (duration <= 125) {
            return "Dengeli Anlatı & Klasik Kurgu" + durationSuffix;
        } else {
            return "Derinlikli, Yavaş Salınımlı & Atmosferik" + durationSuffix;
        }
    }
}
