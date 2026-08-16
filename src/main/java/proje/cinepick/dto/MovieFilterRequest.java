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
public class MovieFilterRequest {
    private List<String> genres;
    private String originalLanguage; // "tr", "en", "all" etc.
    private Integer minYear;
    private Integer maxYear;
    private Double minRating;
    private Integer maxRuntime;
    private String platform;
    private List<String> platforms;

    @Builder.Default
    private Integer limit = 50;

    @Builder.Default
    private Integer page = 0;
}
