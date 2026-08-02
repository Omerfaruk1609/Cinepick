package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieAnalysisResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String atmosphere;
    private String narrativePace;
    private List<String> keyThemes;
    private String whyToWatch;
}
