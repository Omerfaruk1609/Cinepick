package proje.cinepick.dto;

import java.util.List;

public class MovieAnalysisRequest {
    private String title;
    private String overview;
    private List<String> genres;
    private Integer runtime;
    private Double voteAverage;
    private Integer releaseYear;

    public MovieAnalysisRequest() {}

    public MovieAnalysisRequest(String title, String overview, List<String> genres, Integer runtime, Double voteAverage, Integer releaseYear) {
        this.title = title;
        this.overview = overview;
        this.genres = genres;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
}
