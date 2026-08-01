package proje.cinepick.dto;

public class NarrativeResponse {
    private String vibe;
    private String pace;
    private String insight;
    private Double depthScore;

    public NarrativeResponse() {}

    public NarrativeResponse(String vibe, String pace, String insight, Double depthScore) {
        this.vibe = vibe;
        this.pace = pace;
        this.insight = insight;
        this.depthScore = depthScore;
    }

    public String getVibe() {
        return vibe;
    }

    public void setVibe(String vibe) {
        this.vibe = vibe;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getInsight() {
        return insight;
    }

    public void setInsight(String insight) {
        this.insight = insight;
    }

    public Double getDepthScore() {
        return depthScore;
    }

    public void setDepthScore(Double depthScore) {
        this.depthScore = depthScore;
    }
}
