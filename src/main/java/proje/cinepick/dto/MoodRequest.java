package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodRequest {
    private String moodText;
    private String moodTag; // e.g. "energetic", "melancholic", "tense", "romantic"

    @Builder.Default
    private Integer limit = 20;
}
