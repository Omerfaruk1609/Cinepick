package proje.cinepick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TasteAnalyticsDto {
    private int totalMoviesWatched;
    private double obscurityScore; // 0 - 100 arası (0: Popüler Kültür, 100: Tam Bağımsız Sinema)
    private String cinemaPersona;   // Örn: "Gizli Cevher Avcısı (Indie Cinephile)", "Popüler Trend Takipçisi"
    private Map<String, Integer> topGenres;     // Tür Dağılımı (Tür İsmi -> İzlenme Sayısı)
    private Map<String, Integer> topDirectors;  // En Çok İzlenen Yönetmenler
}
