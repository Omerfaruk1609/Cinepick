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
public class MovieAnalysisRequest {
    private String title;
    private String overview;
    private List<String> genres;
    private Integer runtime;
    private Double voteAverage;
    private Integer releaseYear;
}
