package proje.cinepick.dto;

public class MovieAnalysisResponse {
    private String atmosphere;
    private String narrativePace;
    private String whyToWatch;

    public MovieAnalysisResponse() {}

    public MovieAnalysisResponse(String atmosphere, String narrativePace, String whyToWatch) {
        this.atmosphere = atmosphere;
        this.narrativePace = narrativePace;
        this.whyToWatch = whyToWatch;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getNarrativePace() {
        return narrativePace;
    }

    public void setNarrativePace(String narrativePace) {
        this.narrativePace = narrativePace;
    }

    public String getWhyToWatch() {
        return whyToWatch;
    }

    public void setWhyToWatch(String whyToWatch) {
        this.whyToWatch = whyToWatch;
    }
}
