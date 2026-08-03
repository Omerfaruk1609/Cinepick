package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private Double voteAverage;
    private String[] genres;
    private Integer matchPercentage;
    private String recommendationReason;

    public static MovieDto fromEntity(proje.cinepick.entity.Movie movie) {
        if (movie == null) return null;
        return MovieDto.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate())
                .voteAverage(movie.getVoteAverage())
                .genres(movie.getGenres())
                .build();
    }
}
