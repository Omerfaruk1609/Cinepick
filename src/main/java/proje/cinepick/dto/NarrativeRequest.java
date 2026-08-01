package proje.cinepick.dto;

import java.util.List;

public class NarrativeRequest {
    private String overview;
    private Integer runtime;
    private Integer releaseYear;
    private Double voteAverage;
    private List<String> genres;

    public NarrativeRequest() {}

    public NarrativeRequest(String overview, Integer runtime, Integer releaseYear, Double voteAverage, List<String> genres) {
        this.overview = overview;
        this.runtime = runtime;
        this.releaseYear = releaseYear;
        this.voteAverage = voteAverage;
        this.genres = genres;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
