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
    private String originalLanguage;
    private Integer runtime;
    private String country;
    private Integer releaseYear;
    private String streamingPlatforms;
    private Long voteCount;
    private String director;

    public static MovieDto fromEntity(proje.cinepick.entity.Movie movie) {
        if (movie == null) return null;
        return MovieDto.builder()
                .id(movie.getId())
                .tmdbId(movie.getTmdbId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null)
                .voteAverage(movie.getVoteAverage())
                .voteCount(movie.getVoteCount())
                .genres(movie.getGenres())
                .originalLanguage(movie.getOriginalLanguage())
                .runtime(movie.getRuntime())
                .country(movie.getCountry())
                .director(movie.getDirector())
                .releaseYear(movie.getReleaseYear())
                .streamingPlatforms(movie.getStreamingPlatforms())
                .build();
    }
}
