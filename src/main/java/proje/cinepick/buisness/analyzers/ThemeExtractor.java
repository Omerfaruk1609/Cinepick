package proje.cinepick.buisness.analyzers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ThemeExtractor {

    private static final Pattern EXISTENTIALISM = Pattern.compile("(?i).*(varoluş|anlam|hayat|ölüm|felsefe|sorgula).*");
    private static final Pattern TIME_PERCEPTION = Pattern.compile("(?i).*(zaman|gelecek|geçmiş|döngü|rüy|anı).*");
    private static final Pattern MORAL_DILEMMA = Pattern.compile("(?i).*(ahlak|vicdan|seçim|suç|karar|ikilem|sır).*");
    private static final Pattern IDENTITY = Pattern.compile("(?i).*(kimlik|arayı|benlik|insan|gerçek).*");
    private static final Pattern REVENGE_JUSTICE = Pattern.compile("(?i).*(intikam|adalet|hak|savaş|mücadele).*");

    public List<String> extractThemes(String overview, List<String> genres) {
        List<String> themes = new ArrayList<>();
        String text = overview != null ? overview : "";

        if (EXISTENTIALISM.matcher(text).find()) themes.add("Varoluşçuluk");
        if (TIME_PERCEPTION.matcher(text).find()) themes.add("Zaman Algısı");
        if (MORAL_DILEMMA.matcher(text).find()) themes.add("Ahlaki İkilem");
        if (IDENTITY.matcher(text).find()) themes.add("Kimlik Arayışı");
        if (REVENGE_JUSTICE.matcher(text).find()) themes.add("Adalet & İntikam");

        if (themes.isEmpty()) {
            themes.add("İnsan Doğası");
            themes.add("Kişisel Dönüşüm");
            themes.add("Sinematik Kesit");
        }

        return themes;
    }
}
