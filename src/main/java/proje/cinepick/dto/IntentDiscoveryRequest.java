package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntentDiscoveryRequest {
    private String query;
    private String prompt;

    @Builder.Default
    private Integer limit = 20;

    public String getEffectiveQuery() {
        if (query != null && !query.trim().isEmpty()) {
            return query;
        }
        return prompt;
    }
}
