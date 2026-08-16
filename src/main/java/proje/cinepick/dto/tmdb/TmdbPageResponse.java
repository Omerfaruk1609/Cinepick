package proje.cinepick.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbPageResponse {
    private Integer page;
    private List<TmdbItem> results;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_results")
    private Integer totalResults;

    @Data
    public static class TmdbItem {
        private Long id;
        private String title;
        private String overview;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("vote_average")
        private Double voteAverage;

        @JsonProperty("vote_count")
        private Long voteCount;

        @JsonProperty("original_language")
        private String originalLanguage;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;
    }
}
