package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieWizardRequest {
    private String mood;          // örn: "energetic", "melancholic", "tense", "romantic", "thoughtful", "cheerful"
    private String origin;        // "TR", "FOREIGN", "ALL"
    private String era;           // "CLASSIC" (<2000), "MODERN" (2000-2019), "NEW" (>=2020), "ALL"
    private String pace;          // "FAST" (<=105 dk), "SLOW" (>=120 dk), "BALANCED"
    private List<String> platforms; // ["Netflix", "Amazon Prime Video", "Disney Plus", "BluTV", "TOD"]
    private String customPrompt;  // Serbest kullanıcı metni

    @Builder.Default
    private Integer limit = 10;
}
