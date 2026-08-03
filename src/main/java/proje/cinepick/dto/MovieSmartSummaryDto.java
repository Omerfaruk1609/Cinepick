package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSmartSummaryDto {
    private String thirtySecondOverview; // 30 saniyelik spoilersız vurucu özet
    private List<String> keyHighlights;   // En güçlü 3 yönü
    private List<String> forWhom;         // Kime Göre
    private List<String> notForWhom;      // Kime Göre Değil
}
